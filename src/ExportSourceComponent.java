import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.ui.ex.MessagesEx;
import org.jetbrains.annotations.NotNull;

/**
 * Created by admin on 2018/1/31.
 */
public class ExportSourceComponent implements ApplicationComponent {

    String msg;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        String tmp = "ExportSourceComponent";
        return tmp;
    }

    public void showResult() {
        MessagesEx.showInfoMessage(this.msg, "export result!");
    }
}
