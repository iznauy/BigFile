package cn.edu.tsinghua.bigfileserver.dao.daoImpl;

import cn.edu.tsinghua.bigfileserver.dao.MetaDao;
import cn.edu.tsinghua.bigfileserver.po.MetaPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
@Repository
public class MetaDaoImpl implements MetaDao {

    private MetaRepository metaRepository;

    @Override
    public void saveMeta(MetaPO metaPO) {
        metaRepository.save(metaPO);
    }

    @Override
    public MetaPO getMetaByFileId(String fileId) {
        return metaRepository.findFirstByFileId(fileId);
    }

    @Override
    public void decreaseChunkToUpload(String fileId) {
        metaRepository.decreaseChunkToUpload(fileId);
    }

    @Autowired
    public void setMetaRepository(MetaRepository metaRepository) {
        this.metaRepository = metaRepository;
    }
}
