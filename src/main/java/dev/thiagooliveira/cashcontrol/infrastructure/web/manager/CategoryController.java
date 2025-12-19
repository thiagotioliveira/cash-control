package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.category.dto.CreateCategoryCommand;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import dev.thiagooliveira.cashcontrol.domain.user.security.SecurityContext;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.AlertModel;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.CategoryActionSheetModel;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.CategoryListModel;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/protected/categories")
public class CategoryController {

  private final SecurityContext securityContext;
  private final CategoryService categoryService;

  public CategoryController(SecurityContext securityContext, CategoryService categoryService) {
    this.securityContext = securityContext;
    this.categoryService = categoryService;
  }

  @GetMapping
  public String index(Model model) {
    var categories = categoryService.get(securityContext.getUser().organizationId());
    model.addAttribute("categories", new CategoryListModel(categories));
    model.addAttribute("category", new CategoryActionSheetModel("Nova Categoria"));
    return "protected/categories/category-list";
  }

  @PostMapping
  public String postCategory(
      @ModelAttribute CategoryActionSheetModel.CategoryForm form,
      Model model,
      RedirectAttributes redirectAttributes) {
    try {
      categoryService.createCategory(
          new CreateCategoryCommand(
              securityContext.getUser().organizationId(),
              form.getName(),
              form.getHashColor(),
              TransactionType.valueOf(form.getType())));
      redirectAttributes.addFlashAttribute(
          "alert", AlertModel.success("Categoria criada com sucesso!"));
    } catch (DomainException | ApplicationException e) {
      redirectAttributes.addFlashAttribute("alert", AlertModel.error(e.getMessage()));
    }
    return "redirect:/protected/categories";
  }

  @PostMapping("/{categoryId}")
  public String postCategory(
      @PathVariable UUID categoryId,
      @ModelAttribute CategoryActionSheetModel.CategoryForm form,
      Model model,
      RedirectAttributes redirectAttributes) {
    try {
      throw InfrastructureException.badRequest("Edição não implementado!");
    } catch (InfrastructureException e) {
      redirectAttributes.addFlashAttribute("alert", AlertModel.error(e.getMessage()));
      return "redirect:/protected/categories";
    }
  }
}
