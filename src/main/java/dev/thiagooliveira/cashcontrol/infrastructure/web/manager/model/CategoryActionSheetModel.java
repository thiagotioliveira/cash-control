package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.UUID;

public class CategoryActionSheetModel {

  private boolean showTypeInput;
  private final String title;
  private final UUID id;
  private final String name;
  private final String type;
  private final String hashColor;

  public CategoryActionSheetModel(
      String title, UUID id, String name, String hashColor, String type) {
    this.title = title;
    this.showTypeInput = false;
    this.id = id;
    this.name = name;
    this.hashColor = hashColor;
    this.type = type;
  }

  public CategoryActionSheetModel(String title) {
    this.title = title;
    this.showTypeInput = true;
    this.id = null;
    this.name = null;
    this.hashColor = "000000";
    this.type = TransactionType.CREDIT.name();
  }

  public String getTitle() {
    return title;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getHashColor() {
    return hashColor;
  }

  public String getType() {
    return type;
  }

  public boolean isShowTypeInput() {
    return showTypeInput;
  }

  public static class CategoryForm {
    private UUID id;
    private String name;
    private String type;
    private String hashColor;

    public CategoryForm() {}

    public UUID getId() {
      return id;
    }

    public void setId(UUID id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getHashColor() {
      return hashColor;
    }

    public void setHashColor(String hashColor) {
      this.hashColor = hashColor;
    }
  }
}
