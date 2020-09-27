package cn.edu.tsinghua.bigfileserver.dao.daoImpl;

import cn.edu.tsinghua.bigfileserver.po.MetaPO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
public interface MetaRepository extends CrudRepository<MetaPO, Long> {

    MetaPO findFirstByFileId(String fileId);

}
