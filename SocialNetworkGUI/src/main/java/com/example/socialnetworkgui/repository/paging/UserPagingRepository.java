package com.example.socialnetworkgui.repository.paging;

import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.dto.UserFilterDTO;
import com.example.socialnetworkgui.utils.paging.Page;
import com.example.socialnetworkgui.utils.paging.Pageable;

public interface UserPagingRepository extends PagingRepository<Long, User> {

    Page<User> findAllOnPage(Pageable pageable, UserFilterDTO userFilter);

}
