package cn.edu.tsinghua.bigfileserver.dao.daoImpl;

import cn.edu.tsinghua.bigfileserver.po.MetaPO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
public interface MetaRepository extends CrudRepository<MetaPO, Long> {

    MetaPO findFirstByFileId(String fileId);

    @Transactional
    @Modifying
    @Query(value = "update meta set chunks_to_upload = chunks_to_upload - 1 where file_id = ?1", nativeQuery = true)
    void decreaseChunkToUpload(String fileId);

}
