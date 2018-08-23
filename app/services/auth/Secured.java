package services.auth;

/**
 * Created by suzuki-daisuke on 2017/04/14.
 */
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security.Authenticator;

/**
 * セッション管理用のクラス
 */
public class Secured extends Authenticator {
    @Override
    public String getUsername(Context ctx){
        return ctx.session().get("employeeNo");
    }

    @Override
    public Result onUnauthorized(Context ctx){//セッションがない状態で勤怠入力画面に遷移しようとしたとき
        String returnUrl = ctx.request().uri();
        if (returnUrl == null){
            returnUrl="/login";
        }
        ctx.session().put("returnUrl", returnUrl);
        return redirect(controllers.routes.AuthCtl.index());
    }

}
