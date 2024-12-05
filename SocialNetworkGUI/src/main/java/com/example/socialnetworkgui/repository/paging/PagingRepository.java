package com.example.socialnetworkgui.repository.paging;

import com.example.socialnetworkgui.domain.Entity;
import com.example.socialnetworkgui.repository.Repository;
import com.example.socialnetworkgui.utils.paging.Page;
import com.example.socialnetworkgui.utils.paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllOnPage(Pageable pageable);
}
