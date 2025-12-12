package dev.thiagooliveira.cashcontrol.infrastructure.config.mockdata;

import dev.thiagooliveira.cashcontrol.application.account.CreateAccount;
import dev.thiagooliveira.cashcontrol.application.account.dto.CreateAccountCommand;
import dev.thiagooliveira.cashcontrol.application.bank.CreateBank;
import dev.thiagooliveira.cashcontrol.application.bank.dto.CreateBankCommand;
import dev.thiagooliveira.cashcontrol.application.category.CreateCategory;
import dev.thiagooliveira.cashcontrol.application.category.dto.CreateCategoryCommand;
import dev.thiagooliveira.cashcontrol.application.outbound.OrganizationRepository;
import dev.thiagooliveira.cashcontrol.application.user.InviteUser;
import dev.thiagooliveira.cashcontrol.application.user.PasswordUtils;
import dev.thiagooliveira.cashcontrol.application.user.RegisterUser;
import dev.thiagooliveira.cashcontrol.application.user.dto.InviteUserCommand;
import dev.thiagooliveira.cashcontrol.application.user.dto.RegisterUserCommand;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.account.AccountJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.UserEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.UserJpaRepository;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.HashMap;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("mock-data")
public class InitContextMockDataCommandLineRunner implements CommandLineRunner {

  private static final Logger log =
      LoggerFactory.getLogger(InitContextMockDataCommandLineRunner.class);

  private final MockContext context;
  private final UserJpaRepository userJpaRepository;
  private final OrganizationRepository organizationRepository;
  private final AccountJpaRepository accountJpaRepository;

  private final MockDataProperties properties;
  private final RegisterUser registerUser;
  private final InviteUser inviteUser;
  private final CreateCategory createCategory;
  private final CreateBank createBank;
  private final CreateAccount createAccount;

  public InitContextMockDataCommandLineRunner(
      MockContext context,
      UserJpaRepository userJpaRepository,
      OrganizationRepository organizationRepository,
      AccountJpaRepository accountJpaRepository,
      MockDataProperties properties,
      RegisterUser registerUser,
      InviteUser inviteUser,
      CreateCategory createCategory,
      CreateBank createBank,
      CreateAccount createAccount) {
    this.context = context;
    this.userJpaRepository = userJpaRepository;
    this.organizationRepository = organizationRepository;
    this.accountJpaRepository = accountJpaRepository;
    this.properties = properties;
    this.registerUser = registerUser;
    this.inviteUser = inviteUser;
    this.createCategory = createCategory;
    this.createBank = createBank;
    this.createAccount = createAccount;
  }

  @Override
  public void run(String... args) throws Exception {
    var user = userJpaRepository.findAll().stream().findFirst();
    if (user.isPresent()) {
      loadContextFromUser(user.get());
    } else {
      createContext();
    }
    log.info("Context loaded!");
  }

  private void createContext() {
    log.info("Creating context...");
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
                context.setOrganizationId(u.getOrganizationId());
                context.setUserId(u.getId());
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
    var categoryMap = new HashMap<String, UUID>();
    properties
        .getCategories()
        .forEach(
            category -> {
              log.debug(" - {} ({})", category.getName(), category.getType());
              var c =
                  createCategory.execute(
                      new CreateCategoryCommand(
                          context.getOrganizationId(),
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
                          context.getOrganizationId(),
                          bank.getName(),
                          Currency.valueOf(bank.getCurrency())));
              bankMap.put(bank.getName(), b.getId());
            });
    log.debug("Accounts:");
    properties
        .getAccounts()
        .forEach(
            account -> {
              log.debug(" - {} (Bank: {})", account.getName(), account.getBank());
              var a =
                  createAccount.execute(
                      new CreateAccountCommand(
                          context.getOrganizationId(),
                          bankMap.get(account.getBank()),
                          account.getName()));
              context.setAccountId(a.getId());
            });
  }

  private void loadContextFromUser(UserEntity user) {
    log.info("Loading context...");
    context.setUserId(user.getId());
    context.setOrganizationId(user.getOrganizationId());
    var account = accountJpaRepository.findAll().stream().findFirst().orElseThrow();
    context.setAccountId(account.getId());
  }
}
