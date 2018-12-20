/**
 * 
 */
package com.daoben.rfid.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.daoben.rfid.mapper.AssetInLibraryMapper;
import com.daoben.rfid.mapper.AssetInfoMapper;
import com.daoben.rfid.mapper.AssetIoLibraryMapper;
import com.daoben.rfid.mapper.ReadeIoTimeMapper;
import com.daoben.rfid.model.AssetInLibrary;
import com.daoben.rfid.model.AssetInfo;
import com.daoben.rfid.model.AssetIoLibrary;
import com.daoben.rfid.model.ReadeIoTime;

/**
 * @author 文
 *
 * @date 2017年3月1日下午4:43:45
 */
@Service
public class IoLibraryService {
	@Resource
	private AssetIoLibraryMapper assetIoLibraryMapper;
	@Resource
	private ReadeIoTimeMapper readeIoTimeMapper;
	@Resource
	private AssetInLibraryMapper assetInLibraryMapper;
	@Resource
	private AssetInfoMapper assetInfoMapper;

	public int insert() {

		int i = 0;
		AssetIoLibrary assetIoLibrary = null;

		List<ReadeIoTime> findAll = readeIoTimeMapper.findAll();
		for (ReadeIoTime readeIoTime : findAll) {
		//	List<AssetInLibrary> findByTagId = assetInLibraryMapper.findByTagId(readeIoTime.getTag_Id());
			List<AssetInfo> findByTagId = assetInfoMapper.findByTagId(readeIoTime.getTag_Id());
			assetIoLibrary = new AssetIoLibrary(readeIoTime.getTag_Id(), findByTagId.get(0).getRfid_Labelnum(),
					findByTagId.get(0).getAsset_Name(), null, "", 0, 0, "", null, findByTagId.get(0).getAsset_Type());

			List<ReadeIoTime> findInTime = readeIoTimeMapper.findInTime(readeIoTime.getTag_Id());
			List<ReadeIoTime> findOutTime = readeIoTimeMapper.findOutTime(readeIoTime.getTag_Id());

			if (findInTime.get(0).getTag_Time().getTime() < findOutTime.get(0).getTag_Time().getTime()) {
				assetIoLibrary.setInoutlibrary("out");
				// assetIoLibrary.setIo_Time(readeIoTimeMapper.getIOTime(new
				// ReadeIoTime("",id.getInoutlibrary(),id.getTag_Id(),null)));
				System.out.println(readeIoTime.getTag_Id() + "--------out");
			} else {
				assetIoLibrary.setInoutlibrary("in");
				// assetIoLibrary.setIo_Time(readeIoTimeMapper.getIOTime(new
				// ReadeIoTime("",id.getInoutlibrary(),id.getTag_Id(),null)));
				System.out.println(readeIoTime.getTag_Id() + "--------in");
			}

			List<AssetIoLibrary> TagId = assetIoLibraryMapper.findTagId(readeIoTime.getTag_Id());

			if (TagId != null && !TagId.isEmpty()) {
				assetIoLibrary.setIo_Time(readeIoTimeMapper.getIOTime(
						new ReadeIoTime("", TagId.get(0).getInoutlibrary(), TagId.get(0).getTag_Id(), null)));
				assetIoLibraryMapper.update(assetIoLibrary);
				readeIoTimeMapper.delete(readeIoTime.getTag_Id());
				i++;
			} else {
				assetIoLibraryMapper.insertAssetIoLibrary(assetIoLibrary);
				readeIoTimeMapper.delete(readeIoTime.getTag_Id());
				i++;
			}
		}
		return i;
	}

}
