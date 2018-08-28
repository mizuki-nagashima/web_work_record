package models;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * 汎用コード管理マスタ
 */
@Entity
public class MsGeneralCodeAdmin extends CommonModel {

    /**
     * コード種別
     */
    @NotNull
    public String codeType;

    /**
     * コード種別名称
     */
    @NotNull
    public String codeTypeName;

    /**
     * コード種別説明
     */
    public String note;

    /**
     * 任意値説明1
     */
    public String anyValueNote1;

    /**
     * 任意値説明2
     */
    public String anyValueNote2;

    /**
     * 任意値説明3
     */
    public String anyValueNote3;

    /**
     * 任意値説明4
     */
    public String anyValueNote4;

    /**
     * 任意値説明5
     */
    public String anyValueNote5;
}
