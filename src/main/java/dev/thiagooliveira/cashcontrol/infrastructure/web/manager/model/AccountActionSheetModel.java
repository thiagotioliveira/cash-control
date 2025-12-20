package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

public class AccountActionSheetModel {

  private String name;
  private String bankName;

  public AccountActionSheetModel() {}

  public AccountActionSheetModel(String name, String bankName) {
    this.name = name;
    this.bankName = bankName;
  }

  public String getName() {
    return name;
  }

  public String getBankName() {
    return bankName;
  }

  public static class AccountForm {
    private String name;
    private String bankName;

    public AccountForm() {}

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getBankName() {
      return bankName;
    }

    public void setBankName(String bankName) {
      this.bankName = bankName;
    }
  }
}
