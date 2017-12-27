package sunnyday.controller.DAO.mysql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.springframework.stereotype.Repository;

import sunnyday.controller.DAO.IFileDAO;
import sunnyday.controller.util.DBUtil;

/**
 *
 * Description:  文件回传相关操作DAO
 * 0 插入时的状态
 * 10 读取之后的状态
 * 100 传输完成
 */

@Repository(value="mysql_FileDAO")
public class FileDAO extends SimpleDAO implements IFileDAO{
	public void insertFileInfoBi(String file_name, int sms_size, int status, int file_type){
		String sql = "insert into file_info_bi (file_sn ,file_name, try_times ,size ,status,file_type,insert_time ,update_time)" +
				" values( null ,? ,0, ? ,?, ?,now() ,now())";
		Connection conn = null;
		PreparedStatement prep = null;
		try {
			conn = dataSource.getConnection();
			prep = conn.prepareStatement(sql);
			prep.setString(1, file_name);
			prep.setInt(2, sms_size);
			prep.setInt(3, status);
			prep.setInt(4, file_type);
			prep.execute();
		} catch (Exception e) {
			log.error("" , e);
		}finally{
			DBUtil.freeConnection(conn, prep);
		}
	}
	
//	public List<TransFile> getFileToBeTrans(int limit,int file_type){
//		return getTransFileList(FileStatus.FileInit,file_type, limit);
//	}
//	
//	private List<TransFile> getTransFileList(int status ,int file_type,int limit ){
//		String sql = "select file_sn,file_name, try_times,size,status,file_type,insert_time,update_time from file_transback_info where status = ? and file_type = ? limit ?";
//		Connection conn = null;
//		PreparedStatement prep = null;
//		ResultSet rs = null;
//		List<TransFile> result = new ArrayList<TransFile>();
//		try {
//			conn = dataSource.getConnection();
//			prep = conn.prepareStatement(sql);
//			prep.setInt(1, status);
//			prep.setInt(2, file_type);
//			prep.setInt(3, limit);
//			rs = prep.executeQuery();
//			result = (List<TransFile>) ResultUtil.assemble(rs, TransFile.class);
//		} catch (Exception e) {
//			log.error("" , e);
//			e.printStackTrace();
//		}finally{
//			DBUtil.freeConnection(conn, prep);
//		}
//		return result;
//	}
//	
//	public List<TransFile> getTimeOutList(int limit){
//		String sql = "select file_sn,file_name, try_times,size,status,file_type,insert_time,update_time from file_transback_info where insert_time < now() - interval 3 day limit  ?";
//		Connection conn = null;
//		PreparedStatement prep = null;
//		ResultSet rs = null;
//		List<TransFile> result = new ArrayList<TransFile>();
//		try {
//			conn = dataSource.getConnection();
//			prep = conn.prepareStatement(sql);
//			prep.setInt(1, limit);
//			rs = prep.executeQuery();
//			result = (List<TransFile>) ResultUtil.assemble(rs, TransFile.class);
//		} catch (Exception e) {
//			log.error("" , e);
//			e.printStackTrace();
//		}finally{
//			DBUtil.freeConnection(conn, prep);
//		}
//		return result;
//	}
//
//	public void fileBeingTransing(int file_sn){
//		updateFileTransBack(file_sn,FileStatus.FileBeingTransing);
//	}
//	public void setFileTransFinished(int file_sn){
//		updateFileTransBack(file_sn,FileStatus.FileTransFinished);
//	}
//	public void setFileTransFinished(String file_sns,int status){
//		String sql = "update file_transback_info set status = ? where file_sn in (?)";
//		Connection conn = null;
//		PreparedStatement prep = null;
//		try {
//			conn = dataSource.getConnection();
//			prep = conn.prepareStatement(sql);
//			prep.setInt(1, status);
//			prep.setString(2, file_sns);
//			prep.execute();
//		} catch (Exception e) {
//			log.error("" , e);
//			e.printStackTrace();
//		}finally{
//			DBUtil.freeConnection(conn, prep);
//		}
//	}
//	
//	private void updateFileTransBack(int file_sn,int status){
//		String sql = "update file_transback_info set status = ? where file_sn = ?";
//		Connection conn = null;
//		PreparedStatement prep = null;
//		try {
//			conn = dataSource.getConnection();
//			prep = conn.prepareStatement(sql);
//			prep.setInt(1, status);
//			prep.setInt(2, file_sn);
//			prep.execute();
//		} catch (Exception e) {
//			log.error("" , e);
//			e.printStackTrace();
//		}finally{
//			DBUtil.freeConnection(conn, prep);
//		}
//	}
//
//	public List<TransFile> excuteQuery(String sql) {
//		Connection conn = null;
//		PreparedStatement prep = null;
//		ResultSet rs = null;
//		List<TransFile> result = new ArrayList<TransFile>();
//		try {
//			conn = dataSource.getConnection();
//			prep = conn.prepareStatement(sql);
//			rs = prep.executeQuery();
//			result = (List<TransFile>) ResultUtil.assemble(rs, TransFile.class);
//		} catch (Exception e) {
//			log.error("" , e);
//			e.printStackTrace();
//		}finally{
//			DBUtil.freeConnection(conn, prep);
//		}
//		return result;
//	}
//
//	public void executeUpdate(String sql) {
//		Connection conn = null;
//		PreparedStatement prep = null;
//		try {
//			conn = dataSource.getConnection();
//			prep = conn.prepareStatement(sql);
//			prep.execute();
//		} catch (Exception e) {
//			log.error("" , e);
//			e.printStackTrace();
//		}finally{
//			DBUtil.freeConnection(conn, prep);
//		}
//	}
//
//	public List<TransFile> getFileTransFinished(int limit) {
//		String sql = "select file_sn,file_name, try_times,size,status,file_type,insert_time,update_time from file_transback_info where status = ? limit ?";
//		Connection conn = null;
//		PreparedStatement prep = null;
//		ResultSet rs = null;
//		List<TransFile> result = new ArrayList<TransFile>();
//		try {
//			conn = dataSource.getConnection();
//			prep = conn.prepareStatement(sql);
//			prep.setInt(1, FileStatus.FileTransFinished);
//			prep.setInt(2, limit);
//			rs = prep.executeQuery();
//			result = (List<TransFile>) ResultUtil.assemble(rs, TransFile.class);
//		} catch (Exception e) {
//			log.error("" , e);
//			e.printStackTrace();
//		}finally{
//			DBUtil.freeConnection(conn, prep);
//		}
//		return result;
//	}
//
//	public void setFileNotExit(int file_sn) {
//		updateFileTransBack(file_sn,FileStatus.FileNotExit);
//	}
//
//	public void setFileTransErr(int file_sn) {
//		updateFileTransBack(file_sn,FileStatus.FileTransErr);
//	}
//
//	public void setFileWaitForResp(int file_sn) {
//		updateFileTransBack(file_sn,FileStatus.FileBeingTransing);
//		
//	}
	
	
}
