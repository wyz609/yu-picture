package com.yupi.yupicturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.exception.ThrowUtils;
import com.yupi.yupicturebackend.manager.FileManager;
import com.yupi.yupicturebackend.manager.upload.FilePictureUpload;
import com.yupi.yupicturebackend.manager.upload.PictureUploadTemplate;
import com.yupi.yupicturebackend.manager.upload.UrlPictureUpload;
import com.yupi.yupicturebackend.model.dto.file.UploadPictureResult;
import com.yupi.yupicturebackend.model.dto.picture.PictureQueryRequest;
import com.yupi.yupicturebackend.model.dto.picture.PictureReviewRequest;
import com.yupi.yupicturebackend.model.dto.picture.PictureUploadByBatchRequest;
import com.yupi.yupicturebackend.model.dto.picture.PictureUploadRequest;
import com.yupi.yupicturebackend.model.entity.Picture;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.enums.PictureReviewStatusEnum;
import com.yupi.yupicturebackend.model.vo.PictureVO;
import com.yupi.yupicturebackend.model.vo.UserVO;
import com.yupi.yupicturebackend.service.PictureService;
import com.yupi.yupicturebackend.mapper.PictureMapper;
import com.yupi.yupicturebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.photo.Photo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jay
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-05-18 14:11:28
*/
@Slf4j
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{


    @Resource
    private FilePictureUpload filePictureUpload;

    @Resource
    private UrlPictureUpload urlPictureUpload;

    @Resource
    private UserService userService;

    /**
     * 上传图片功能
     * 该方法允许用户上传图片，无论是新增还是更新图片
     * @param inputSource 用户选择的图片文件
     * @param pictureUploadRequest 包含上传图片的请求信息，如图片ID
     * @param loginUser 当前登录的用户信息，用于验证身份和确定存储路径
     * @return 返回上传后的图片信息，包括URL等
     */
@Override
public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {

    ThrowUtils.throwIf(inputSource == null, ErrorCode.PARAMS_ERROR,"图片为空");

    // 校验参数
    ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
    // 用于判断是新增还是更新图片
    Long pictureId = null;
    if(pictureUploadRequest != null){
       pictureId = pictureUploadRequest.getId();
    }

    // 如果是更新图片，确认图片是否存在
    if(pictureId != null){
        Picture oldPicture = this.getById(pictureId);
        ThrowUtils.throwIf(oldPicture == null,ErrorCode.NOT_FOUND_ERROR,"图片不存在");
        // 仅本人或管理员可编辑
        if(!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)){
            // 如果图片存在，但是不是当前用户上传的，抛出异常
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"无权更新该图片");
        }
//        boolean exists = this.lambdaQuery()
//                .eq(Picture::getId,pictureId)
//                .exists();
//        ThrowUtils.throwIf(!exists,ErrorCode.NOT_FOUND_ERROR,"图片不存在");
    }
    // 上传图片
    // 按照用户id划分目录，以确保用户只能上传图片到自己的目录中
    String uploadPathPrefix = String.format("public/%s", loginUser.getId());
    // 根据inputSource类型区分上传方式
    PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
    if(inputSource instanceof String){
        pictureUploadTemplate = urlPictureUpload;
    }
    UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
    // 构造要入库的图片信息
    Picture picture = getPicture(loginUser, uploadPictureResult, pictureId);

    String picName = uploadPictureResult.getPicName();
    assert pictureUploadRequest != null;
    if(StrUtil.isNotBlank(pictureUploadRequest.getPicName())){
        picName = pictureUploadRequest.getPicName();
    }
    picture.setName(picName);
    // 保存或更新图片信息到数据库
    boolean result = this.saveOrUpdate(picture);
    ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR,"图片上传失败");

    // 返回上传后的图片信息
    return PictureVO.objToVo(picture);
}


    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if(pictureQueryRequest == null){
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long reviewerId = pictureQueryRequest.getReviewUserId();

        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus),"reviewStatus",reviewStatus);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);

        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        PictureVO pictureVO = PictureVO.objToVo(picture);
        Long userId = picture.getUserId();
        if(userId != null && userId > 0){
            // 获取用户信息
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUser(userVO);
        }

        return pictureVO;
    }

    /**
     * 分页获取图片封装
     * @param picturePage page对象
     * @param request request请求
     * @return
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if(CollUtil.isEmpty(pictureList)){
            return pictureVOPage;
        }
        // 对象列表 -> 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy((User::getId)));
        // 2. 对封装对象列表进行赋值
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if(userIdUserListMap.containsKey(userId)){
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;


    }

    /**
     * 图片校验，用于更新和修改图片时进行判断
     * @param picture 图片
     */
    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        // 对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id不能为空，有参数则校验
        ThrowUtils.throwIf(ObjUtil.isNull(id),ErrorCode.PARAMS_ERROR,"id不能为空");
        if(StrUtil.isNotBlank(url)){
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR,"url过长");
        }
        if(StrUtil.isNotBlank(introduction)){
            ThrowUtils.throwIf(introduction.length() > 100, ErrorCode.PARAMS_ERROR,"简介过长");
        }
    }

    /**
     * 图片审核功能
     * @param pictureReviewRequest 图片审核请求体
     * @param loginUser 登录用户
     */
    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // 获取图片请求体中的图片ID和审核结果
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        // 校验参数
        if(id == null || reviewStatusEnum == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断图片是否存在
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        // 检查是否重复审核
        if(oldPicture.getReviewStatus().equals(reviewStatus)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请勿重复审核");
        }

        // 更新带审核状态
        Picture updatePicture = new Picture();
        BeanUtils.copyProperties(pictureReviewRequest,updatePicture);
        updatePicture.setReviewerId(loginUser.getId());
        updatePicture.setReviewTime(new Date());
        boolean result = this.updateById(updatePicture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片审核失败");

    }

    /**
     * 根据用户信息和上传图片结果创建或更新图片对象
     *
     * @param loginUser 当前登录的用户，用于关联图片与用户
     * @param uploadPictureResult 图片上传结果，包含图片的各种信息
     * @param pictureId 图片ID，如果为null表示新增图片，否则表示更新现有图片
     * @return 返回一个新的图片对象，或者更新后的图片对象
     */
    private Picture getPicture(User loginUser, UploadPictureResult uploadPictureResult, Long pictureId) {
        // 创建一个新的图片对象
        Picture picture = new Picture();
        // 设置图片的URL
        picture.setUrl(uploadPictureResult.getUrl());
        // 设置图片的名称
        picture.setName(uploadPictureResult.getPicName());
        // 设置图片的大小
        picture.setPicSize(uploadPictureResult.getPicSize());
        // 设置图片的宽度
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        // 设置图片的高度
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        // 设置图片的缩放比例
        picture.setPicScale(uploadPictureResult.getPicScale());
        // 设置图片的格式
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        // 设置图片所属的用户ID
        picture.setUserId(loginUser.getId());


        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        // 补充审核参数
        fillReviewParams(picture,loginUser);

        // 如果pictureId不为空，表示更新，否则是新增
        if(pictureId != null){
            // 打印更新图片的信息
            System.out.println("更新图片，ID: " + pictureId);
            // 如果是更新，需要补充id和编辑时间
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        System.out.println("上传图片信息: " + picture);
        // 返回图片对象
        return picture;
    }

    /**
     * 该方法用来判断上传图片者的身份，并设置相应的审核状态和信息。
     * @param picture
     * @param loginUser
     */
    @Override
    public void fillReviewParams(Picture picture, User loginUser){
        // 验证登录用户是否为管理员
        if (userService.isAdmin(loginUser)) {
            // 如果是管理员，则自动过审
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewTime(new Date());
            picture.setReviewMessage("管理员自动过审");
        }else {
            // 如果不是管理员，则设置为待审核状态
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
            picture.setReviewMessage("待审核");
        }
    }

    /**
 * 批量上传图片，使用爬虫抓取关键字图片进行批量下载
 * @param pictureUploadByBatchRequest 图片批量上传请求体
 * @param loginUser 登录用户
 * @return 图片数量
 */
@Override
public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
    System.out.println("批量上传图片");
    // 获取搜索文本,搜索框关键字
    String searchText = pictureUploadByBatchRequest.getSearchText();
    String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
    if(StrUtil.isBlank(namePrefix)){
        namePrefix = searchText;
    }
    // 格式化数量
    Integer count = pictureUploadByBatchRequest.getCount();
    // 校验数量范围，如果超出预设范围则抛出异常
    ThrowUtils.throwIf(count > 30 || count < 1, ErrorCode.PARAMS_ERROR, "数量超出范围");
    // 偏移量，可以更具需求动态设置
    int offset = RandomUtil.randomInt(1000);
    // 构造要抓取的地址
    String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1&first=%d", searchText,offset);
    Document document;
    try{
        // 发送网络请求获取图片页面文档，Bing图片搜索可能对first参数有上限(可能最多翻到1000张图片)，频繁请求可能触发Bing的反爬机制，添加随机化的User-Agent
        document = Jsoup.connect(fetchUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .get();
    }catch (IOException e){
        // 如果发生IO异常，则记录错误日志并抛出业务异常
        log.error("获取图片失败",e);
        throw new BusinessException(ErrorCode.OPERATION_ERROR,"获取图片失败");
    }
    // 获取包含图片的div元素
    Element div = document.getElementsByClass("dgControl").first();
    // 如果未找到指定的div元素，则抛出业务异常
    if(ObjUtil.isNull(div)){
        throw new BusinessException(ErrorCode.OPERATION_ERROR,"获取元素失败");
    }
    // 获取所有图片元素
//    Elements imgElementList = div.select("img.mimg");
    Elements imgElementList = div.select(".iusc");
    int uploadCount = 0;
    // 遍历图片元素列表
    for (Element imgElement : imgElementList) {
        // 获取图片URL
//        String imgUrl = imgElement.attr("src");
        // 获取data-m属性中的JSON字符串
        String dataM = imgElement.attr("m");
        String imgUrl;
        try{
            // 解析JSON字符串并获取图片URL
            JSONObject jsonObject = JSONUtil.parseObj(dataM);
            // 获取murl字段(原始图片URL)
            imgUrl = jsonObject.getStr("murl");
        }catch (Exception e){
            // 如果解析失败，则记录错误日志并跳过当前图片
            log.error("解析图片URL失败",e);
            continue;
        }
        // 如果图片URL为空，则记录日志并跳过当前图片
        if(StrUtil.isBlank(imgUrl)){
            log.info("当前链接为空，已跳过:{}",imgUrl);
            continue;
        }
        // 处理图片上传地址，防止出现转义问题
        int questionMarkIndex = imgUrl.indexOf("?");
        if(questionMarkIndex > -1){
            imgUrl = imgUrl.substring(0,questionMarkIndex);
        }
        // 创建图片上传请求对象
        PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
        if(StrUtil.isNotBlank(namePrefix)){
            // 设置图片名称，序号连续递增
            pictureUploadRequest.setPicName(namePrefix + (uploadCount + 1));
        }
        try{
            // 调用上传图片方法，如果上传成功则增加上传计数
            PictureVO pictureVO = this.uploadPicture(imgUrl, pictureUploadRequest, loginUser);
            log.info("图片上传成功，id={}",pictureVO.getId());
            uploadCount ++;
        }catch (Exception e){
            // 如果上传失败，则记录错误日志并继续处理下一张图片
            log.error("图片上传失败",e);
            continue;
        }
        // 如果上传成功的图片数量达到预设数量，则停止处理
        if (uploadCount >= count){
            break;
        }
    }
    // 返回上传成功的图片数量
    return uploadCount;
}

}




