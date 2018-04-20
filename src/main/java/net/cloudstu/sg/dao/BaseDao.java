package net.cloudstu.sg.dao;


import java.util.List;

public interface BaseDao<M, QM> {
    void create(M m);

    void update(M m);

//    void delete(int id);

    M getById(int id);

    List<M> getByCondition(QM qm);

    void clear();
}
