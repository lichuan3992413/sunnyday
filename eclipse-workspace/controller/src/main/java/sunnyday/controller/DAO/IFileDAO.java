package sunnyday.controller.DAO;

import org.springframework.stereotype.Repository;

/**
 *
 * Description:  文件回传相关操作DAO
 * 0 插入时的状态
 * 10 读取之后的状态
 * 100 传输完成
 */

@Repository
public interface IFileDAO {
	public void insertFileInfoBi(String file_name, int sms_size, int status, int file_type);
}
