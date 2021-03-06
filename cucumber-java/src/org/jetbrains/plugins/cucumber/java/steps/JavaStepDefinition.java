package org.jetbrains.plugins.cucumber.java.steps;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.cucumber.java.CucumberJavaUtil;

public class JavaStepDefinition extends AbstractJavaStepDefinition {
  private final String myAnnotationClassName;

  public JavaStepDefinition(@NotNull PsiElement stepDef, @NotNull String annotationClassName) {
    super(stepDef);
    myAnnotationClassName = annotationClassName;
  }

  @Nullable
  @Override
  protected String getCucumberRegexFromElement(PsiElement element) {
    if (!(element instanceof PsiMethod)) {
      return null;
    }
    final PsiAnnotation stepAnnotation = CucumberJavaUtil.getCucumberStepAnnotation((PsiMethod)element, myAnnotationClassName);
    if (stepAnnotation == null) {
      return null;
    }
    final PsiElement annotationValue = CucumberJavaUtil.getAnnotationValue(stepAnnotation);
    if (annotationValue == null) {
      return null;
    }
    final PsiConstantEvaluationHelper evaluationHelper = JavaPsiFacade.getInstance(element.getProject()).getConstantEvaluationHelper();
    final Object constantValue = evaluationHelper.computeConstantExpression(annotationValue, false);
    if (constantValue != null) {
      String patternText = constantValue.toString();
      if (patternText.length() > 1) {
        return patternText.replace("\\\\", "\\").replace("\\\"", "\"");
      }
    }

    return null;
  }
}
