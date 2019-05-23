package com.ej.restaurant.model;

import com.ej.restaurant.enums.DataStatus;
import com.ej.restaurant.utils.DLClock;
import com.ej.restaurant.utils.UUIDUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

public abstract class DataEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    protected String id;
    protected String remarks;	// 备注

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Long createDate;	// 创建日期
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Long updateDate;	// 更新日期
    @Setter
    @Getter
    protected DataStatus status=DataStatus.USEABLE; 	// 默认可用



    public DataEntity() {
    }

    public DataEntity(String id) {
        this.id= id;
    }


    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 插入之前执行方法，需要手动调用
     */
    public void preInsert(){

        this.setId(UUIDUtil.generateUUID());
        this.updateDate = DLClock.now();
        this.createDate = DLClock.now();
    }
    /**
     * 更新之前执行方法，需要手动调用
     */
    public void preUpdate(){
        this.updateDate = DLClock.now();
    }
}
