package com.ej.restaurant.mapper;

import java.util.List;

public interface BaseMapper<T> {
    /**
     * 获取单条数据
     * @param id
     * @return
     */
    public T get(String id);

    /**
     * 查询所有数据列表
     * @see public List<T> findAllList(T entity)
     * @return
     */
    public List<T> findAllList();

    /**
     * 插入数据
     * @param entity
     * @return
     */
    public int insert(T entity);

    /**
     * 更新数据
     * @param entity
     * @return
     */
    public int update(T entity);

    /**
     * @param id
     * @see public int delete(T entity)
     * @return
     */
    public int delete(String id);

    /**
     * @param entity
     * @return
     */
    public int delete(T entity);
}
