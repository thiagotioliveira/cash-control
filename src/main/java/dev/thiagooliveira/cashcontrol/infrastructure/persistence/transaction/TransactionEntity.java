package dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction;

import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.*;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.account.AccountEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.category.CategoryEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.UserEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils;
import dev.thiagooliveira.cashcontrol.shared.TransactionStatus;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private UUID organizationId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "confirmed_by")
  private UserEntity user;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  private AccountEntity account;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transaction_template_id")
  private TransactionTemplateEntity transactionTemplate;

  @Column private Instant occurredAt;

  @Column(nullable = false)
  private LocalDate originalDueDate;

  @Column(nullable = false)
  private LocalDate dueDate;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column private BigDecimal accountBalance;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private CategoryEntity category;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionStatus status;

  public TransactionEntity() {}

  public TransactionEntity(TransactionRequested event) {
    this.id = event.transactionId();
    this.organizationId = event.organizationId();
    this.user = null;
    this.account = new AccountEntity();
    this.account.setId(event.accountId());
    this.transactionTemplate = null;
    this.occurredAt = null;
    this.originalDueDate = event.occurredAt().atZone(FormattersUtils.zoneId).toLocalDate();
    this.dueDate = this.originalDueDate;
    this.description = event.description();
    this.amount = event.amount();
    this.accountBalance = null;
    this.category = new CategoryEntity();
    this.category.setId(event.categoryId());
    this.type = event.type();
    this.status = TransactionStatus.PENDING;
  }

  public TransactionEntity(ScheduledTransactionCreated event) {
    this.id = event.transactionId();
    this.organizationId = event.organizationId();
    this.account = new AccountEntity();
    this.account.setId(event.accountId());
    this.transactionTemplate = new TransactionTemplateEntity();
    this.transactionTemplate.setId(event.templateId());
    this.occurredAt = null;
    this.originalDueDate = event.dueDate();
    this.dueDate = this.originalDueDate;
    this.description = event.description();
    this.amount = event.amount();
    this.category = new CategoryEntity();
    this.category.setId(event.categoryId());
    this.type = event.type();
    this.status = TransactionStatus.SCHEDULED;
  }

  public TransactionSummary toDomain() {
    return new TransactionSummary(
        this.id,
        this.account.getId(),
        this.account.getName(),
        this.user != null ? Optional.of(this.user.getId()) : Optional.empty(),
        this.user != null ? Optional.ofNullable(this.user.getName()) : Optional.empty(),
        this.account.getBank().getCurrency(),
        this.transactionTemplate != null
            ? Optional.of(this.transactionTemplate.getId())
            : Optional.empty(),
        Optional.ofNullable(this.occurredAt),
        this.dueDate,
        this.description,
        this.amount,
        Optional.ofNullable(this.accountBalance),
        this.category.getId(),
        this.category.getName(),
        this.category.getHashColor(),
        this.type,
        this.status,
        this.transactionTemplate != null
            ? Optional.ofNullable(this.transactionTemplate.getRecurrence())
            : Optional.empty(),
        this.transactionTemplate != null
            ? Optional.ofNullable(this.transactionTemplate.getTotalInstallments())
            : Optional.empty());
  }

  public void confirm(TransactionConfirmed event) {
    this.occurredAt = event.occurredAt();
    this.accountBalance = event.balanceAfter();
    this.status = TransactionStatus.CONFIRMED;
    this.user = new UserEntity();
    this.user.setId(event.userId());
  }

  public void update(ScheduledTransactionUpdated event) {
    this.description = event.description();
    this.amount = event.amount();
    this.dueDate = event.dueDate();
  }

  public void updateStatusToPending() {
    if (!TransactionStatus.SCHEDULED.equals(this.status)) {
      throw InfrastructureException.conflict("Status of transaction is " + this.status);
    }
    this.status = TransactionStatus.PENDING;
  }

  public boolean wasScheduled() {
    return this.transactionTemplate != null;
  }

  public void revert() {
    this.occurredAt = null;
    this.accountBalance = null;
    this.user = null;
    this.status = TransactionStatus.SCHEDULED;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Instant getOccurredAt() {
    return occurredAt;
  }

  public void setOccurredAt(Instant occurredAt) {
    this.occurredAt = occurredAt;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }

  public TransactionStatus getStatus() {
    return status;
  }

  public void setStatus(TransactionStatus status) {
    this.status = status;
  }

  public CategoryEntity getCategory() {
    return category;
  }

  public void setCategory(CategoryEntity category) {
    this.category = category;
  }

  public UUID getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(UUID organizationId) {
    this.organizationId = organizationId;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public TransactionTemplateEntity getTransactionTemplate() {
    return transactionTemplate;
  }

  public void setTransactionTemplate(TransactionTemplateEntity transactionTemplate) {
    this.transactionTemplate = transactionTemplate;
  }

  public AccountEntity getAccount() {
    return account;
  }

  public LocalDate getOriginalDueDate() {
    return originalDueDate;
  }

  public void setOriginalDueDate(LocalDate originalDueDate) {
    this.originalDueDate = originalDueDate;
  }

  public void setAccount(AccountEntity account) {
    this.account = account;
  }
}
