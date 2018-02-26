import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangesSelection;
import com.intellij.openapi.vcs.changes.ChangesUtil;
import org.apache.commons.httpclient.util.DateUtil;
import util.FileUtil;

import java.io.File;
import java.util.Calendar;
import java.util.List;

/**
 * Created by liwj on 2018/1/31.
 */
public class ExportSourceAction extends AnAction {

    public static final String SOURCE_PATCH_ROOT_FOLDER = "source-patch";
    private String projectPath;
   /* private final String java_source_path = "\\src\\main\\java";
    private final String conf_path= "\\src\\main\\resources";
    private final String page_patch = "\\WebRoot";
    private final String web_path = "";*/
    private String wfTemplete = "";
    private String logFile;
    private String pathFolder;
    private StringBuffer copyFile = new StringBuffer("已导出文件：\n");
    private StringBuffer noExistFile = new StringBuffer("\n\n不存在的文件[导出前需要先编译]：\n");
    private StringBuffer noCopyFile = new StringBuffer("\n\n不需要导出的文件：");
    private int copyFileCount ;
    private int noExistFileCount ;
    private int noCopyFileCount ;

    @Override
    public void actionPerformed(AnActionEvent event) {
        copyFileCount = noExistFileCount = noCopyFileCount = 0;
        Project project = event.getData(CommonDataKeys.PROJECT);
        this.projectPath = processPath(project.getBasePath());
        this.wfTemplete = (this.projectPath + "\\templates\\workflowTemplate");
        this.logFile = (this.projectPath + File.separator + getTodayPatchFolder() + File.separator + getFormatDateString("YYYYMMddHHmmss") + File.separator + "log.txt");
        this.pathFolder = (this.projectPath + File.separator + getTodayPatchFolder() + File.separator + getFormatDateString("YYYYMMddHHmmss"));

        ChangesSelection changesSelection = event.getData(VcsDataKeys.CHANGES_SELECTION);

        if (changesSelection != null) {
            List<Change> changes = changesSelection.getChanges();
            for (Change change : changes) {
                FilePath filePath = ChangesUtil.getFilePath(change);
                copyFile(processPath(filePath.getPath()));
            }
        }
        FileUtil.writeExportLog(this.logFile, this.copyFile);
        FileUtil.writeExportLog(this.logFile, this.noExistFile);
        FileUtil.writeExportLog(this.logFile, this.noCopyFile);
        Application application = ApplicationManager.getApplication();
        ExportSourceComponent exportSourceComponent = application.getComponent(ExportSourceComponent.class);
        String msg = "已导出文件:" + this.copyFileCount + "个\n";
        msg = msg + "不存在文件:" + this.noExistFileCount + "个\n";
        msg = msg + "不需要导出文件:" + this.noCopyFileCount + "个\n";
        msg = msg + "详细内容请看日志文件：\n" + this.logFile;
        exportSourceComponent.setMsg(msg);
        exportSourceComponent.showResult();
    }

    private boolean copyFile(String src) {
        String desc ;
        String outFile ;
        boolean needCopy ;
        if (src.contains(this.wfTemplete)) {
            desc = src.replace(this.wfTemplete, getTargetPath("\\workflowTemplate"));
            outFile = desc.replace(getTargetPath(""), "");
            needCopy = true;
        }
        else {
            desc = src.replace(getProjectPath(""), getTargetPath(""));
            outFile = desc.replace(getTargetPath(""), "");
            needCopy = true;
        }
        try {
            if (!needCopy) {
                this.noCopyFile.append(src.replace(this.projectPath, "")).append("\n");
                this.noCopyFileCount += 1;
                return false;
            }
            FileUtil.copyFile(src, desc);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        this.copyFile.append(outFile).append("\n");
        this.copyFileCount += 1;
        return true;
    }

    private String getTargetPath(String path) {
        return this.pathFolder + path;
    }

    private String processPath(String filePath) {
        if ("\\".equals(File.separator)) {
            filePath = filePath.replaceAll("/", "\\\\");
        }
        return filePath;
    }
    private String getProjectPath(String itemPath) {
        return this.projectPath + itemPath;
    }

    private static String getTodayPatchFolder() {
        return SOURCE_PATCH_ROOT_FOLDER + File.separator + DateUtil.formatDate(Calendar.getInstance().getTime(),"YYYYMMdd");
    }
    private static String getFormatDateString(String pattern) {
        //根据格式返回特定格式的日期字符串
        String primaryPattern = "YYYYMMDD";
        return DateUtil.formatDate(Calendar.getInstance().getTime(), pattern == "" || pattern == null ? primaryPattern : pattern);
    }
}
