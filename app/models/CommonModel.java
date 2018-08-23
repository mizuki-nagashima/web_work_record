package models;

import java.sql.Timestamp;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;

/**
 * 共通カラム
 */
@MappedSuperclass
public class CommonModel extends Model {

    /**
     * 登録ユーザID
     */
    @NotNull
    public String registUserId;

    /**
     * 登録日時
     */
    @NotNull
    @CreatedTimestamp
    public Timestamp registDate;

    /**
     * 更新ユーザID
     */
    public String updateUserId;

    /**
     * 更新日時
     */
    @UpdatedTimestamp
    public Timestamp updateDate;

}
