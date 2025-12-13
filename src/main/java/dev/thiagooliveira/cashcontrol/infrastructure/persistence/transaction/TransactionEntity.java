package dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import dev.thiagooliveira.cashcontrol.domain.event.account.ScheduledTransactionUpdated;
import dev.thiagooliveira.cashcontrol.domain.event.account.TransactionConfirmed;
import dev.thiagooliveira.cashcontrol.domain.event.account.TransactionCreated;
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

  public TransactionEntity(TransactionCreated event) {
    this.id = event.getTransactionId();
    this.organizationId = event.getOrganizationId();
    this.user = new UserEntity();
    this.user.setId(event.getUserId());
    this.account = new AccountEntity();
    this.account.setId(event.getAccountId());
    this.transactionTemplate = null;
    this.occurredAt = event.occurredAt();
    this.originalDueDate = this.occurredAt.atZone(FormattersUtils.zoneId).toLocalDate();
    this.dueDate = this.originalDueDate;
    this.description = event.getDescription();
    this.amount = event.getAmount();
    this.accountBalance = event.getBalanceAfter();
    this.category = new CategoryEntity();
    this.category.setId(event.getCategoryId());
    this.type = event.getType();
    this.status = TransactionStatus.CONFIRMED;
  }

  public TransactionEntity(TransactionTemplateEntity template, LocalDate dueDate) {
    this.id = UUID.randomUUID();
    this.organizationId = template.getOrganizationId();
    this.account = new AccountEntity();
    this.account.setId(template.getAccountId());
    this.transactionTemplate = template;
    this.occurredAt = null;
    this.originalDueDate = dueDate;
    this.dueDate = this.originalDueDate;
    this.description = template.getDescription();
    this.amount = template.getAmount();
    this.category = new CategoryEntity();
    this.category.setId(template.getCategoryId());
    this.type = template.getType();
    this.status = TransactionStatus.SCHEDULED;
  }

  public GetTransactionItem toDomain() {
    return new GetTransactionItem(
        this.id,
        this.account.getId(),
        this.account.getName(),
        this.user != null ? Optional.of(this.user.getId()) : Optional.empty(),
        this.user != null ? Optional.of(this.user.getName()) : Optional.empty(),
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
    this.amount = event.getAmount();
    this.accountBalance = event.getBalanceAfter();
    this.status = TransactionStatus.CONFIRMED;
    this.user = new UserEntity();
    this.user.setId(event.getUserId());
  }

  public void update(ScheduledTransactionUpdated event) {
    this.description = event.description();
    this.amount = event.amount();
    this.dueDate = event.dueDate();
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
