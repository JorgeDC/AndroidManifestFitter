package be.jorgedc.androidmanifestfitter;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.PsiTreeUtil;

import java.io.Console;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: Jorge
 * Date: 08/08/13
 * Time: 20:08
 */

public class AndroidManifestFitter extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        VirtualFile vFile = DataKeys.VIRTUAL_FILE.getData(dataContext);
        VirtualFile parent = vFile.getParent();
        String packageName = "";
        if (parent != null) {
            int i = 100;
            while (i > 0 && parent != null && (!parent.getName().equals("main") && (!parent.getName().equals("java")) && (!parent.getName().equals("src")))) {
                packageName = parent.getName() + "." + packageName;
                parent = parent.getParent();
                i--;
            }
        }
        String className = vFile.getPresentableName().replace(".java", "");
        packageName += className;
        VirtualFile virtualFile[] = parent.getParent().getChildren();
        for (int i = 0; i < virtualFile.length; i++) {
            VirtualFile childFile = virtualFile[i];
            Document document = FileDocumentManager.getInstance().getCachedDocument(childFile);
            if (document != null && document.isWritable() && childFile.getPresentableName().toLowerCase().equals("androidmanifest.xml")) {
                String androidManifest = document.getCharsSequence().toString();
                androidManifest = androidManifest.replace("</application>", "\n <activity android:name=\"" + packageName + "\" /> \n\n </application>");
                Runnable writeAction = new WriteAction(androidManifest, document);
                ApplicationManager.getApplication().runWriteAction(writeAction);
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            e.getPresentation().setEnabled(false);
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
        if (psiClass != null) {
            int i = 20;
            while (i > 0 && psiClass.getSuperClass() != null) {
                if (psiClass.getSuperClass().toString().toLowerCase().equals("psiclass:activity")) {
                    e.getPresentation().setEnabled(true);
                    return;
                }
                psiClass = psiClass.getSuperClass();
                i--;
            }
            e.getPresentation().setEnabled(false);
        }
        e.getPresentation().setEnabled(false);
    }

}
