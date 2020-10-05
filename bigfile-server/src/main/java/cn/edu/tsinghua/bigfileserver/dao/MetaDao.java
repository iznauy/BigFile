package cn.edu.tsinghua.bigfileserver.dao;

import cn.edu.tsinghua.bigfileserver.po.MetaPO;

/**
 * Created on 2020-09-24.
 * Description:
 *
 * @author iznauy
 */
public interface MetaDao {

    void saveMeta(MetaPO metaPO);

    MetaPO getMetaByFileId(String fileId);

    void decreaseChunkToUpload(String fileId);

}
