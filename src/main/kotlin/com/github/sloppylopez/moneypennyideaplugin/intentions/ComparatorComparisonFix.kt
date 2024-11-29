package com.github.sloppylopez.moneypennyideaplugin.intentions

import com.intellij.codeInsight.intention.IntentionManager
import com.intellij.codeInspection.CommonQuickFixBundle
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.dataFlow.value.RelationType
import com.intellij.java.JavaBundle
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiBinaryExpression
import com.intellij.psi.util.PsiTreeUtil
import com.siyeh.ig.psiutils.CommentTracker
import org.jetbrains.annotations.Nls


private class ComparatorComparisonFix(
    private val myYodaCondition: Boolean,
    private val myRelation: RelationType
) : LocalQuickFix {
    override fun getName(): @Nls String {
        return CommonQuickFixBundle.message("fix.replace.with.x", replacement)
    }

    private val replacement: String
        get() = if (myYodaCondition) "0 " + myRelation.flipped else "$myRelation 0"

    override fun getFamilyName(): @Nls String {
        return JavaBundle.message("inspection.comparator.result.comparison.fix.family.name")
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val binOp = PsiTreeUtil.getParentOfType(
            descriptor.startElement,
            PsiBinaryExpression::class.java
        ) ?: return
        val ct = CommentTracker()
        val replacement: String = if (myYodaCondition) {
            val operand = binOp.rOperand ?: return
            this.replacement + ct.text(operand)
        } else {
            ct.text(binOp.lOperand) + this.replacement
        }
        ct.replaceAndRestoreComments(binOp, replacement)
    }

//    fun register(){
//        val intentionManager = IntentionManager.getInstance()
//        intentionManager.addAction(this)
//    }
}
