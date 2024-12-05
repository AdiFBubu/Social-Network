package com.example.socialnetworkgui.repository.paging;

import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.dto.UserFilterDTO;
import com.example.socialnetworkgui.utils.paging.Page;
import com.example.socialnetworkgui.utils.paging.Pageable;

public interface FriendshipPagingRepository extends PagingRepository<Tuple<Long, Long>, Friendship> {
    Page<Friendship> findAllOnPage(Pageable pageable, UserFilterDTO friendshipFilter);

}
