package dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction;

import dev.thiagooliveira.cashcontrol.domain.event.account.ScheduledTransactionCreated;
import dev.thiagooliveira.cashcontrol.domain.event.account.ScheduledTransactionUpdated;
import dev.thiagooliveira.cashcontrol.shared.DueDateUtils;
import dev.thiagooliveira.cashcontrol.shared.Recurrence;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transaction_templates")
public class TransactionTemplateEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private UUID organizationId;

  @Column(nullable = false)
  private UUID accountId;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private UUID categoryId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Recurrence recurrence;

  @Column(nullable = false)
  private LocalDate originalStartDate;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column private LocalDate endDate;

  @Column private Integer totalInstallments;

  public TransactionTemplateEntity() {}

  public TransactionTemplateEntity(ScheduledTransactionCreated event) {
    this.id = event.transactionId();
    this.organizationId = event.organizationId();
    this.accountId = event.accountId();
    this.description = event.description();
    this.amount = event.amount();
    this.categoryId = event.categoryId();
    this.type = event.type();
    this.recurrence = event.recurrence();
    this.originalStartDate = event.dueDate();
    this.startDate = this.originalStartDate;
    if (event.totalInstallments() != null) {
      this.totalInstallments = event.totalInstallments();
      var dueDate = event.dueDate();
      for (int i = 0; i < this.totalInstallments; i++) {
        this.endDate = dueDate;
        dueDate = DueDateUtils.nextDueDate(dueDate, event.recurrence());
      }
    }
  }

  public void update(ScheduledTransactionUpdated event) {
    this.amount = event.amount();
    this.startDate = event.dueDate();
    this.endDate = event.endDueDate();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
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

  public Recurrence getRecurrence() {
    return recurrence;
  }

  public void setRecurrence(Recurrence recurrence) {
    this.recurrence = recurrence;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(UUID categoryId) {
    this.categoryId = categoryId;
  }

  public Integer getTotalInstallments() {
    return totalInstallments;
  }

  public void setTotalInstallments(Integer totalInstallments) {
    this.totalInstallments = totalInstallments;
  }

  public UUID getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(UUID organizationId) {
    this.organizationId = organizationId;
  }

  public LocalDate getOriginalStartDate() {
    return originalStartDate;
  }

  public void setOriginalStartDate(LocalDate originalStartDate) {
    this.originalStartDate = originalStartDate;
  }
}
