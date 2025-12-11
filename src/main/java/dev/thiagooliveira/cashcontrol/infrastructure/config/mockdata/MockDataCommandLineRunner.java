package dev.thiagooliveira.cashcontrol.infrastructure.config.mockdata;

import dev.thiagooliveira.cashcontrol.application.account.*;
import dev.thiagooliveira.cashcontrol.application.account.dto.*;
import dev.thiagooliveira.cashcontrol.application.bank.CreateBank;
import dev.thiagooliveira.cashcontrol.application.bank.dto.CreateBankCommand;
import dev.thiagooliveira.cashcontrol.application.category.CreateCategory;
import dev.thiagooliveira.cashcontrol.application.category.dto.CreateCategoryCommand;
import dev.thiagooliveira.cashcontrol.application.outbound.CategoryRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.OrganizationRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.GetTransactions;
import dev.thiagooliveira.cashcontrol.application.user.InviteUser;
import dev.thiagooliveira.cashcontrol.application.user.PasswordUtils;
import dev.thiagooliveira.cashcontrol.application.user.RegisterUser;
import dev.thiagooliveira.cashcontrol.application.user.dto.InviteUserCommand;
import dev.thiagooliveira.cashcontrol.application.user.dto.RegisterUserCommand;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.HashMap;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(prefix = "app.mock-data", name = "enabled", havingValue = "true")
public class MockDataCommandLineRunner implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(MockDataCommandLineRunner.class);

  private final MockDataProperties properties;

  private final OrganizationRepository organizationRepository;
  private final CategoryRepository categoryRepository;

  private final RegisterUser registerUser;
  private final InviteUser inviteUser;
  private final CreateBank createBank;
  private final CreateAccount createAccount;
  private final CreateCategory createCategory;
  private final CreateDeposit createDeposit;
  private final CreateWithdrawal createWithdrawal;
  private final CreatePayable createPayable;
  private final CreateReceivable createReceivable;
  private final GetTransactions getTransactions;
  private final UpdateScheduledTransaction updateScheduledTransaction;
  private final ConfirmTransaction confirmTransaction;

  public MockDataCommandLineRunner(
      MockDataProperties properties,
      OrganizationRepository organizationRepository,
      TransactionRepository transactionRepository,
      CategoryRepository categoryRepository,
      RegisterUser registerUser,
      InviteUser inviteUser,
      CreateBank createBank,
      CreateAccount createAccount,
      CreateCategory createCategory,
      CreateDeposit createDeposit,
      CreateWithdrawal createWithdrawal,
      CreatePayable createPayable,
      CreateReceivable createReceivable,
      GetTransactions getTransactions,
      UpdateScheduledTransaction updateScheduledTransaction,
      ConfirmTransaction confirmTransaction) {
    this.properties = properties;
    this.organizationRepository = organizationRepository;
    this.categoryRepository = categoryRepository;
    this.registerUser = registerUser;
    this.inviteUser = inviteUser;
    this.getTransactions = getTransactions;
    this.createBank = createBank;
    this.createAccount = createAccount;
    this.createCategory = createCategory;
    this.createDeposit = createDeposit;
    this.createWithdrawal = createWithdrawal;
    this.createPayable = createPayable;
    this.createReceivable = createReceivable;
    this.updateScheduledTransaction = updateScheduledTransaction;
    this.confirmTransaction = confirmTransaction;
  }

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    log.debug("Loading mock data...");
    log.debug("User:");
    properties
        .getUsers()
        .forEach(
            user -> {
              if (user.getOrganization() == null) {
                var password = PasswordUtils.generatePassword();
                var u =
                    registerUser.execute(
                        new RegisterUserCommand(
                            user.getName(), user.getEmail(), password, password));
                properties.setOrganizationId(u.getOrganizationId());
                properties.setUserId(u.getId());
                log.debug(
                    " - {} ({}) - Password: '{}' - Organization: {}",
                    user.getName(),
                    user.getEmail(),
                    password,
                    u.getOrganizationId());
              } else {
                var organization =
                    organizationRepository.findByEmail(user.getOrganization()).orElseThrow();
                log.debug(
                    " - Inviting {} to organization {}", user.getName(), organization.getId());
                var u =
                    inviteUser.execute(
                        new InviteUserCommand(
                            user.getName(), user.getEmail(), organization.getId()));
              }
            });
    log.debug("Categories:");
    var categoryMap = new HashMap<String, UUID>();
    properties
        .getCategories()
        .forEach(
            category -> {
              log.debug(" - {} ({})", category.getName(), category.getType());
              var c =
                  createCategory.execute(
                      new CreateCategoryCommand(
                          properties.getOrganizationId(),
                          category.getName(),
                          category.getHashColor(),
                          TransactionType.valueOf(category.getType())));
              categoryMap.put(category.getName(), c.getId());
            });
    log.debug("Banks:");
    var bankMap = new HashMap<String, UUID>();
    properties
        .getBanks()
        .forEach(
            bank -> {
              log.debug(" - {} ({})", bank.getName(), bank.getCurrency());
              var b =
                  createBank.execute(
                      new CreateBankCommand(
                          properties.getOrganizationId(),
                          bank.getName(),
                          Currency.valueOf(bank.getCurrency())));
              bankMap.put(bank.getName(), b.getId());
            });

    var categoryInitialCredit =
        categoryRepository
            .findByOrganizationIdAndNameAndType(
                properties.getOrganizationId(), "Outros", TransactionType.CREDIT)
            .orElseThrow();

    log.debug("Accounts:");
    properties
        .getAccounts()
        .forEach(
            account -> {
              log.debug(" - {} (Bank: {})", account.getName(), account.getBank());
              var a =
                  createAccount.execute(
                      new CreateAccountCommand(
                          properties.getOrganizationId(),
                          bankMap.get(account.getBank()),
                          account.getName()));
              properties.setAccountId(a.getId());
            });
  }
}
