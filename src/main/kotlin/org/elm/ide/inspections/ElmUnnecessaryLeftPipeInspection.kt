package org.elm.ide.inspections

import com.intellij.codeInspection.ProblemHighlightType.LIKE_UNUSED_SYMBOL
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.elm.lang.core.psi.*
import org.elm.lang.core.psi.elements.*

/**
 * Find unused functions, parameters, etc.
 */
class ElmUnnecessaryLeftPipeInspection : ElmLocalInspection() {

    override fun visitElement(element: ElmPsiElement, holder: ProblemsHolder, isOnTheFly: Boolean) {
        if (element !is ElmOperator) return
        if (element.text != "<|") return

        val binOpExpr = element.parent as? ElmBinOpExpr ?: return
        if (binOpExpr.parts.count() != 3) return

        val next = element.nextSiblings.withoutWsOrComments.firstOrNull()
                ?: return

        when (next) {
            is ElmListExpr,
            is ElmRecordExpr,
            is ElmParenthesizedExpr,
            is ElmUnitExpr,
            is ElmStringConstantExpr ->
                holder.registerProblem(
                        element,
                        "'<|' is redundant",
                        LIKE_UNUSED_SYMBOL,
                        SafelyDeleteLeftPipeFix()
                )
        }
    }
}

private class SafelyDeleteLeftPipeFix : NamedQuickFix("Safely Delete <|") {
    override fun applyFix(element: PsiElement, project: Project) {
        if (element.parent !is ElmBinOpExpr) return
        if (element !is ElmOperator) return

        val left = element.prevSiblings.withoutWs.firstOrNull() ?: return
        val right = element.nextSiblings.withoutWs.firstOrNull() ?: return

        val expr = ElmPsiFactory(project).createFunctionCallExpr("${left.text} ${right.text}")
        element.parent.replace(expr)
    }
}
