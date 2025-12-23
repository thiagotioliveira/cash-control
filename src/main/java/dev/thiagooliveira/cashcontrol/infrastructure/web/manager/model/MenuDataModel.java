package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

public class MenuDataModel {
  private String active;

  private MenuDataModel(String active) {
    this.active = active;
  }

  public static MenuDataModel home() {
    return new MenuDataModel("HOME");
  }

  public static MenuDataModel categories() {
    return new MenuDataModel("CATEGORIES");
  }

  public static MenuDataModel reports() {
    return new MenuDataModel("REPORTS");
  }

  public String getActive() {
    return active;
  }
}
