package dev.thiagooliveira.cashcontrol.infrastructure.config.mockdata;

import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.mock-data")
@ConditionalOnProperty(prefix = "app.mock-data", name = "enabled", havingValue = "true")
public class MockDataProperties {
  private boolean enabled = false;
  private List<User> users;
  private List<Category> categories;
  private List<Bank> banks;
  private List<Account> accounts;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }

  public List<Bank> getBanks() {
    return banks;
  }

  public void setBanks(List<Bank> banks) {
    this.banks = banks;
  }

  public List<Account> getAccounts() {
    return accounts;
  }

  public void setAccounts(List<Account> accounts) {
    this.accounts = accounts;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  public static class User {
    private String name;
    private String email;
    private String organization;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getOrganization() {
      return organization;
    }

    public void setOrganization(String organization) {
      this.organization = organization;
    }
  }

  public static class Category {
    private String name;
    private String hashColor;
    private String type;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getHashColor() {
      return hashColor;
    }

    public void setHashColor(String hashColor) {
      this.hashColor = hashColor;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }

  public static class Bank {
    private String name;
    private String currency;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getCurrency() {
      return currency;
    }

    public void setCurrency(String currency) {
      this.currency = currency;
    }
  }

  public static class Account {
    private String name;
    private String bank;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getBank() {
      return bank;
    }

    public void setBank(String bank) {
      this.bank = bank;
    }
  }
}
