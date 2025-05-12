package com.ruoyi.pay.service.wallet;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.common.enums.CommonStatusEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.pay.convert.wallet.PayWalletRechargePackageConvert;
import com.ruoyi.pay.domain.wallet.PayWalletRechargePackageDO;
import com.ruoyi.pay.mapper.wallet.PayWalletRechargePackageMapper;
import com.ruoyi.pay.service.vo.WalletRechargePackageCreateReqVO;
import com.ruoyi.pay.service.vo.WalletRechargePackagePageReqVO;
import com.ruoyi.pay.service.vo.WalletRechargePackageUpdateReqVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 钱包充值套餐 Service 实现类
 *
 * @author jason
 */
@Service
public class PayWalletRechargePackageServiceImpl implements PayWalletRechargePackageService {

    @Resource
    private PayWalletRechargePackageMapper walletRechargePackageMapper;

    @Override
    public PayWalletRechargePackageDO getWalletRechargePackage(Long packageId) {
        return walletRechargePackageMapper.selectById(packageId);
    }

    @Override
    public PayWalletRechargePackageDO validWalletRechargePackage(Long packageId) {
        PayWalletRechargePackageDO rechargePackageDO = walletRechargePackageMapper.selectById(packageId);
        if (rechargePackageDO == null) {
            throw new ServiceException("钱包充值套餐不存在");
        }
        if (CommonStatusEnum.DISABLE.getStatus().equals(rechargePackageDO.getStatus())) {
            throw new ServiceException("钱包充值套餐已禁用");
        }
        return rechargePackageDO;
    }

    @Override
    public Long createWalletRechargePackage(WalletRechargePackageCreateReqVO createReqVO) {
        // 校验套餐名是否唯一
        validateRechargePackageNameUnique(null, createReqVO.getName());

        // 插入
        PayWalletRechargePackageDO walletRechargePackage = PayWalletRechargePackageConvert.INSTANCE.convert(createReqVO);
        walletRechargePackageMapper.insert(walletRechargePackage);
        // 返回
        return walletRechargePackage.getId();
    }

    @Override
    public void updateWalletRechargePackage(WalletRechargePackageUpdateReqVO updateReqVO) {
        // 校验存在
        validateWalletRechargePackageExists(updateReqVO.getId());
        // 校验套餐名是否唯一
        validateRechargePackageNameUnique(updateReqVO.getId(), updateReqVO.getName());

        // 更新
        PayWalletRechargePackageDO updateObj = PayWalletRechargePackageConvert.INSTANCE.convert(updateReqVO);
        walletRechargePackageMapper.updateById(updateObj);
    }

    private void validateRechargePackageNameUnique(Long id, String name) {
        if (StrUtil.isBlank(name)) {
            return;
        }
        PayWalletRechargePackageDO rechargePackage = walletRechargePackageMapper.selectByName(name);
        if (rechargePackage == null) {
            return ;
        }
        if (id == null) {
            throw new ServiceException("钱包充值套餐名称已存在");
        }
        if (!id.equals(rechargePackage.getId())) {
            throw new ServiceException("钱包充值套餐名称已存在");
        }
    }

    @Override
    public void deleteWalletRechargePackage(Long id) {
        // 校验存在
        validateWalletRechargePackageExists(id);
        // 删除
        walletRechargePackageMapper.deleteById(id);
    }

    private void validateWalletRechargePackageExists(Long id) {
        if (walletRechargePackageMapper.selectById(id) == null) {
            throw new ServiceException("钱包充值套餐不存在");
        }
    }

    @Override
    public PageResult<PayWalletRechargePackageDO> getWalletRechargePackagePage(WalletRechargePackagePageReqVO pageReqVO) {
        return walletRechargePackageMapper.selectPage(pageReqVO);
    }

    @Override
    public List<PayWalletRechargePackageDO> getWalletRechargePackageList(Integer status) {
        return walletRechargePackageMapper.selectListByStatus(status);
    }

}
