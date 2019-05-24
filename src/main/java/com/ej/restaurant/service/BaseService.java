package com.ej.restaurant.service;

import com.ej.restaurant.mapper.BaseMapper;
import com.ej.restaurant.model.DataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public abstract class BaseService<D extends BaseMapper<T>, T extends DataEntity> {
    /**
     * 持久层对象
     */
    @Autowired
    protected D dao;

    /**
     * 获取单条数据
     * @param id
     * @return
     */
    public T get(String id) {
        return dao.get(id);
    }

    /**
     * 保存数据（插入）
     * @param entity
     * @return
     */
    @Transactional(readOnly = false)
    public void insert(T entity) {
        entity.preInsert();
        dao.insert(entity);
    }
    @Transactional(readOnly = false)
    public void update(T entity) {
        entity.preUpdate();
        dao.update(entity);
    }


    /**
     * 删除数据
     * @param id
     */
    @Transactional(readOnly = false)
    public void delete(String id) {
        dao.delete(id);
    }
    /**
     * 删除数据
     * @param entity
     */
    @Transactional(readOnly = false)
    public void delete(T entity) {
        dao.delete(entity);
    }
}
