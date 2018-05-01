package org.blackdog.linkguardian.repository.search.dummy;

import java.util.Collections;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public abstract class AbstractSearchRepositoryImpl<T> implements ElasticsearchRepository<T, Long> {

    @Override
    public <S extends T> S index(S entity) {
        return null;
    }

    @Override
    public Iterable<T> search(QueryBuilder query) {
        return Collections.emptyList();
    }

    @Override
    public Page<T> search(QueryBuilder query, Pageable pageable) {
        return new PageImpl<T>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Page<T> search(SearchQuery searchQuery) {
        return new PageImpl<T>(Collections.emptyList(), new PageRequest(0, 100), 0);
    }

    @Override
    public Page<T> searchSimilar(T entity, String[] fields, Pageable pageable) {
        return new PageImpl<T>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public void refresh() {

    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        return Collections.emptyList();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return new PageImpl<T>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public <S extends T> S save(S entity) {
        return null;
    }

    @Override
    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        return Collections.emptyList();
    }

    @Override
    public T findOne(Long k) {
        return null;
    }

    @Override
    public boolean exists(Long k) {
        return false;
    }

    @Override
    public Iterable<T> findAll() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<T> findAll(Iterable<Long> ks) {
        return Collections.emptyList();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void delete(T entity) {

    }

    @Override
    public void delete(Iterable<? extends T> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
