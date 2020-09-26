package cn.edu.tsinghua.bigfileserver.service.meta;

import cn.edu.tsinghua.bigfilecommon.vo.BasicMetaVO;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
public interface MetaService {

    BasicMetaVO createFile(String id, long size);



}
