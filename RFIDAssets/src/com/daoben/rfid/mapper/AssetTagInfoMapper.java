package com.daoben.rfid.mapper;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.stereotype.Repository;

import com.daoben.rfid.model.AssetTagInfo;

@Repository
public interface AssetTagInfoMapper {
	public List<AssetTagInfo> selectAllTagInfo();

	public List<AssetTagInfo> selectByTagId(String tagId);// 返回AssetTagInfo信息

	public List<AssetTagInfo> selectTagInfoWarn(String tagId);// 返回电量不足的AssetTagInfo信息

	public Timestamp selectByTagIdTime(String tagId);

	public int insertPartTagInfo(AssetTagInfo record);

	int updatePartByTagId(AssetTagInfo record);

	int updatePartLoseTagId(String tag_id);

	int updateAllTagIdInfo(List<String> tag_id);

	int deleteByTagId(String tagId);

}