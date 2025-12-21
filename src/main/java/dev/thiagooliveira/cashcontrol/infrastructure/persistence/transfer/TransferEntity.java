package dev.thiagooliveira.cashcontrol.infrastructure.persistence.transfer;

import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.TransferRequested;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.account.AccountEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.category.CategoryEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.UserEntity;
import dev.thiagooliveira.cashcontrol.shared.TransferStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transfers")
public class TransferEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private UUID organizationId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id_from", nullable = false)
  private AccountEntity accountFrom;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id_to", nullable = false)
  private AccountEntity accountTo;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private CategoryEntity category;

  @Column(nullable = false)
  private Instant occurredAt;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private BigDecimal amountFrom;

  @Column(nullable = false)
  private BigDecimal amountTo;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransferStatus status;

  public TransferEntity() {}

  public TransferEntity(TransferRequested event) {
    this.id = event.id();
    this.organizationId = event.organizationId();
    this.user = new UserEntity();
    this.user.setId(event.userId());
    this.accountFrom = new AccountEntity();
    this.accountFrom.setId(event.accountIdFrom());
    this.accountTo = new AccountEntity();
    this.accountTo.setId(event.accountIdTo());
    this.category = new CategoryEntity();
    this.category.setId(event.categoryId());
    this.occurredAt = event.occurredAt();
    this.description = event.description();
    this.amountFrom = event.amountFrom();
    this.amountTo = event.amountTo();
    this.status = TransferStatus.PENDING;
  }

  public TransferStatus increaseStatus() {
    if (this.status == TransferStatus.PENDING) {
      this.status = TransferStatus.STARTED;
      return this.status;
    } else if (this.status == TransferStatus.STARTED) {
      return TransferStatus.CONFIRMED;
    } else if (this.status == TransferStatus.CONFIRMED) {
      throw InfrastructureException.conflict("Transfer already confirmed");
    } else {
      throw InfrastructureException.conflict("something went wrong");
    }
  }

  public void markAsConfirmed() {
    this.status = TransferStatus.CONFIRMED;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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

  public AccountEntity getAccountFrom() {
    return accountFrom;
  }

  public void setAccountFrom(AccountEntity accountFrom) {
    this.accountFrom = accountFrom;
  }

  public AccountEntity getAccountTo() {
    return accountTo;
  }

  public void setAccountTo(AccountEntity accountTo) {
    this.accountTo = accountTo;
  }

  public CategoryEntity getCategory() {
    return category;
  }

  public void setCategory(CategoryEntity category) {
    this.category = category;
  }

  public Instant getOccurredAt() {
    return occurredAt;
  }

  public void setOccurredAt(Instant occurredAt) {
    this.occurredAt = occurredAt;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getAmountFrom() {
    return amountFrom;
  }

  public void setAmountFrom(BigDecimal amountFrom) {
    this.amountFrom = amountFrom;
  }

  public BigDecimal getAmountTo() {
    return amountTo;
  }

  public void setAmountTo(BigDecimal amountTo) {
    this.amountTo = amountTo;
  }

  public TransferStatus getStatus() {
    return status;
  }

  public void setStatus(TransferStatus status) {
    this.status = status;
  }
}
