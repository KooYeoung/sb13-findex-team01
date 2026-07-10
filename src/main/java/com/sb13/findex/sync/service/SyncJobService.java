package com.sb13.findex.sync.service;

import com.sb13.findex.indexdata.dto.CursorPageResponse;
import com.sb13.findex.sync.dto.SyncJobDto;
import com.sb13.findex.sync.dto.SyncJobSearchCondition;

public interface SyncJobService {
    CursorPageResponse<SyncJobDto> search(SyncJobSearchCondition condition);
}
