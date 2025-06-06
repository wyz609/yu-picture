package com.yupi.yupicturebackend.service.impl; // 定义当前类所在的包名，表示服务层的实现类

import cn.hutool.core.collection.CollUtil; // 导入 Hutool 工具包中的集合工具类，用于判断集合是否为空
import cn.hutool.core.util.ObjUtil; // 导入 Hutool 工具包中的对象工具类，用于判断对象是否为空
import cn.hutool.core.util.RandomUtil; // 导入 Hutool 工具包中的随机工具类 (在此代码片段中未使用)
import cn.hutool.core.util.StrUtil; // 导入 Hutool 工具包中的字符串工具类，用于判断字符串是否为空
import cn.hutool.json.JSONObject; // 导入 Hutool 工具包中的 JSON 对象类，用于解析 JSON 字符串
import cn.hutool.json.JSONUtil; // 导入 Hutool 工具包中的 JSON 工具类，用于 JSON 字符串和对象之间的转换
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper; // 导入 MyBatis-Plus 的查询包装器，用于构建数据库查询条件
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入 MyBatis-Plus 的分页对象
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl; // 导入 MyBatis-Plus 的通用 Service 实现基类
import com.yupi.yupicturebackend.exception.BusinessException; // 导入自定义的业务异常类
import com.yupi.yupicturebackend.exception.ErrorCode; // 导入自定义的错误码枚举
import com.yupi.yupicturebackend.exception.ThrowUtils; // 导入自定义的异常抛出工具类
import com.yupi.yupicturebackend.manager.CosManager; // 导入自定义的 COS 管理器，用于与腾讯云 COS 交互
import com.yupi.yupicturebackend.manager.upload.FilePictureUpload; // 导入文件图片上传策略类
import com.yupi.yupicturebackend.manager.upload.PictureUploadTemplate; // 导入图片上传模板抽象类
import com.yupi.yupicturebackend.manager.upload.UrlPictureUpload; // 导入 URL 图片上传策略类
import com.yupi.yupicturebackend.model.dto.file.UploadPictureResult; // 导入图片上传结果数据传输对象
import com.yupi.yupicturebackend.model.dto.picture.*;
import com.yupi.yupicturebackend.model.entity.Picture; // 导入图片实体类
import com.yupi.yupicturebackend.model.entity.Space;
import com.yupi.yupicturebackend.model.entity.User; // 导入用户实体类
import com.yupi.yupicturebackend.model.enums.PictureReviewStatusEnum; // 导入图片审核状态枚举
import com.yupi.yupicturebackend.model.vo.PictureVO; // 导入图片视图对象 (VO)
import com.yupi.yupicturebackend.model.vo.UserVO; // 导入用户视图对象 (VO)
import com.yupi.yupicturebackend.service.PictureService; // 导入图片服务接口
import com.yupi.yupicturebackend.mapper.PictureMapper; // 导入图片 Mapper 接口
import com.yupi.yupicturebackend.service.SpaceService;
import com.yupi.yupicturebackend.service.UserService; // 导入用户服务接口
import lombok.extern.slf4j.Slf4j; // 导入 Lombok 的 Slf4j 注解，用于自动生成日志记录器
import org.jsoup.Jsoup; // 导入 Jsoup 库，用于 HTML 解析和网页抓取
import org.jsoup.nodes.Document; // 导入 Jsoup 的 Document 类，表示 HTML 文档
import org.jsoup.nodes.Element; // 导入 Jsoup 的 Element 类，表示 HTML 元素
import org.jsoup.select.Elements; // 导入 Jsoup 的 Elements 类，表示 HTML 元素集合
import org.springframework.beans.BeanUtils; // 导入 Spring 的 BeanUtils 工具类，用于对象属性拷贝
import org.springframework.scheduling.annotation.Async; // 导入 Spring 的异步执行注解
import org.springframework.stereotype.Service; // 导入 Spring 的 Service 注解，标识这是一个服务组件
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource; // 导入 Java EE 的资源注入注解
import javax.servlet.http.HttpServletRequest; // 导入 Servlet 的 HttpServletRequest 接口
import java.io.IOException; // 导入 IO 异常类
import java.util.Date; // 导入 Java 日期类
import java.util.List; // 导入 Java 集合框架的 List 接口
import java.util.Map; // 导入 Java 集合框架的 Map 接口
import java.util.Set; // 导入 Java 集合框架的 Set 接口
import java.util.stream.Collectors; // 导入 Java Stream API 的收集器

/**
 * @author jay
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * 该类实现了图片相关的各种业务逻辑，包括上传、查询、审核、清理和批量上传等。
 * @createDate 2025-05-18 14:11:28
 */
@Slf4j // Lombok 注解，自动为当前类生成一个名为 'log' 的 Slf4j Logger 实例，用于记录日志
@Service // 标识当前类为一个 Spring Service 组件，Spring 会自动扫描并将其注册为 Bean
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> // 继承 MyBatis-Plus 的 ServiceImpl，提供基本的 CRUD 操作
        implements PictureService{ // 实现自定义的 PictureService 接口


    @Resource // 注入 FilePictureUpload 实例，用于处理文件上传的业务逻辑
    private FilePictureUpload filePictureUpload;

    @Resource // 注入 UrlPictureUpload 实例，用于处理 URL 图片上传的业务逻辑
    private UrlPictureUpload urlPictureUpload;

    @Resource // 注入 UserService 实例，用于处理用户相关的业务逻辑，如获取用户信息、判断管理员权限
    private UserService userService;

    @Resource // 自动装配 CosManager 实例，用于与腾讯云 COS 进行交互
    private CosManager cosManager;

    @Resource
    private SpaceService spaceService;

    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * 上传图片功能
     * 该方法允许用户上传图片，无论是新增图片还是更新现有图片。
     * 它根据输入源的类型（文件或URL）选择不同的上传策略，并处理图片入库和权限校验。
     *
     * @param inputSource 用户选择的图片文件（MultipartFile）或图片URL（String）。
     * @param pictureUploadRequest 包含上传图片的请求信息，如图片ID（用于更新）、图片名称等。
     * @param loginUser 当前登录的用户信息，用于验证身份和确定存储路径，以及后续的审核流程。
     * @return PictureVO 返回上传后的图片信息视图对象，包括URL、名称等。
     */
    @Override // 覆盖 PictureService 接口中的 uploadPicture 方法
    public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {

        // 校验：如果输入源为空，则抛出参数错误异常，提示“图片为空”。
        ThrowUtils.throwIf(inputSource == null, ErrorCode.PARAMS_ERROR,"图片为空");

        // 校验：如果当前登录用户为空，则抛出未授权异常。
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);

        // 校验空间是否存在
        Long spaceId = pictureUploadRequest.getSpaceId();
        if(spaceId != null){
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null,ErrorCode.NOT_FOUND_ERROR,"空间不存在");
            // 必须空间创建人(管理员)才能上传
            if(!loginUser.getId().equals(space.getUserId())){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"没有空间权限");
            }

            // 校验额度
            if(space.getTotalCount() >= space.getMaxCount()){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"空间条数不足");
            }
            if (space.getTotalSize() >= space.getMaxSize()){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"空间大小不足");
            }

        }

        // 初始化图片ID，用于判断是新增还是更新操作。
        Long pictureId = null;
        // 如果图片上传请求不为空，尝试获取其中的图片ID。
        pictureId = pictureUploadRequest.getId();

        // 如果 pictureId 不为空，说明是更新现有图片的操作。
        if(pictureId != null){
            // 根据图片ID从数据库中查询旧的图片信息。
            Picture oldPicture = this.getById(pictureId);
            // 校验：如果旧图片不存在，则抛出未找到异常。
            ThrowUtils.throwIf(oldPicture == null,ErrorCode.NOT_FOUND_ERROR,"图片不存在");
            // 权限校验：只有图片上传者本人或管理员才能更新该图片。
            if(!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)){
                // 如果图片存在，但不是当前用户上传的且当前用户不是管理员，则抛出无权操作异常。
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"无权更新该图片");
            }
            // 校验空间是否一致
            // 没有穿 spaceId 则复用原有图片的 spaceId
            if(spaceId == null){
                if(oldPicture.getSpaceId() != null){
                    spaceId = oldPicture.getSpaceId();
                }
            }else{
                // 传了 spaceId, 必须和原有图片一致
                if(ObjUtil.notEqual(spaceId,oldPicture.getSpaceId())){
                    throw new BusinessException(ErrorCode.PARAMS_ERROR,"空间不id一致");
                }
            }
        }
        // 上传图片到 COS。
        // 按照用户ID划分 COS 存储目录，确保用户只能上传图片到自己的专属目录中，便于管理和权限控制。
        // 按照用户 id划分目录 => 按照空间划分目录
        String uploadPathPrefix;
        if(spaceId == null){
            // 默认上传路径前缀为 "public/用户ID"
            uploadPathPrefix = String.format("public/%s", loginUser.getId());
        }else {
            // 默认上传路径前缀为 "space/空间ID"
            uploadPathPrefix = String.format("space/%s",spaceId);
        }
        // 根据 inputSource 的类型（String 表示 URL，否则默认为文件）选择不同的图片上传策略。
        PictureUploadTemplate pictureUploadTemplate = filePictureUpload; // 默认使用文件上传策略
        if(inputSource instanceof String){ // 如果输入源是 String 类型，则认为是 URL
            pictureUploadTemplate = urlPictureUpload; // 切换为 URL 图片上传策略
        }
        // 调用选定的图片上传策略的 uploadPicture 方法执行实际的上传操作。
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
        // 构造要保存或更新到数据库的图片实体对象。
        Picture picture = getPicture(loginUser, uploadPictureResult, pictureId,spaceId);

        // 获取上传结果中的图片名称。
        String picName = uploadPictureResult.getPicName();
        // 断言 pictureUploadRequest 不为空，确保后续操作安全。
        assert pictureUploadRequest != null;
        // 如果请求中提供了图片名称，则使用请求中的名称覆盖自动生成的名称。
        if(StrUtil.isNotBlank(pictureUploadRequest.getPicName())){
            picName = pictureUploadRequest.getPicName();
        }
        // 设置图片实体对象的名称。
        picture.setName(picName);

        // 开启事务
        Long finalSpaceId = spaceId;
        transactionTemplate.execute(status -> {
            // 将图片信息保存或更新到数据库。
            // saveOrUpdate 方法会根据 picture.getId() 是否为空来判断是执行插入还是更新操作。
            boolean result = this.saveOrUpdate(picture);
            // 校验：如果保存或更新操作失败，则抛出操作失败异常。
            ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR,"图片上传失败");
            if(finalSpaceId != null){
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, finalSpaceId)
                        .setSql("totalSize = totalSize" + picture.getPicSize())
                        .setSql("totalCount = totalCount + 1")
                        .update();
                ThrowUtils.throwIf(!update,ErrorCode.OPERATION_ERROR,"额度更新失败");
            }
            return picture;
        });
        // 返回上传后的图片信息视图对象。
        return PictureVO.objToVo(picture);
    }


    /**
     * 构建图片查询的 QueryWrapper。
     * 该方法根据 PictureQueryRequest 中的条件动态构建 MyBatis-Plus 的查询包装器，
     * 用于数据库查询操作，支持多字段搜索、精确匹配、模糊匹配和 JSON 数组查询。
     *
     * @param pictureQueryRequest 图片查询请求对象，包含各种查询条件。
     * @return QueryWrapper<Picture> 构建好的查询包装器。
     */
    @Override // 覆盖 PictureService 接口中的 getQueryWrapper 方法
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>(); // 创建一个新的查询包装器实例
        // 如果查询请求对象为空，则直接返回空的查询包装器。
        if(pictureQueryRequest == null){
            return queryWrapper;
        }
        // 从图片查询请求对象中提取各个查询参数的值。
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
        String searchText = pictureQueryRequest.getSearchText(); // 搜索文本，用于多字段模糊查询
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField(); // 排序字段
        String sortOrder = pictureQueryRequest.getSortOrder(); // 排序顺序（ascend/descend）
        Integer reviewStatus = pictureQueryRequest.getReviewStatus(); // 审核状态
        String reviewMessage = pictureQueryRequest.getReviewMessage(); // 审核信息
        Long reviewerId = pictureQueryRequest.getReviewUserId(); // 审核人ID
        Long spaceId = pictureQueryRequest.getSpaceId(); // 空间ID
        boolean nullSpaceId = pictureQueryRequest.isNullSpaceId();// 空间id是否为空


        // 多字段搜索：如果 searchText 不为空，则在名称和简介字段中进行模糊查询。
        if (StrUtil.isNotBlank(searchText)) {
            // 使用 and 方法拼接条件，确保括号内的条件作为一个整体。
            queryWrapper.and(qw -> qw.like("name", searchText) // 名称字段模糊匹配 searchText
                    .or() // 或者
                    .like("introduction", searchText) // 简介字段模糊匹配 searchText
            );
        }
        // 精确匹配：如果 id 不为空，则添加 id 等于 id 的条件。
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        // 精确匹配：如果 userId 不为空，则添加 userId 等于 userId 的条件。
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        // 模糊匹配：如果 name 不为空，则添加 name 模糊匹配 name 的条件。
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        // 模糊匹配：如果 introduction 不为空，则添加 introduction 模糊匹配 introduction 的条件。
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        // 模糊匹配：如果 picFormat 不为空，则添加 picFormat 模糊匹配 picFormat 的条件。
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        // 精确匹配：如果 category 不为空，则添加 category 等于 category 的条件。
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        // 精确匹配：如果 picWidth 不为空，则添加 picWidth 等于 picWidth 的条件。
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        // 精确匹配：如果 picHeight 不为空，则添加 picHeight 等于 picHeight 的条件。
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        // 精确匹配：如果 picSize 不为空，则添加 picSize 等于 picSize 的条件。
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        // 精确匹配：如果 picScale 不为空，则添加 picScale 等于 picScale 的条件。
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        // 精确匹配：如果 reviewStatus 不为空，则添加 reviewStatus 等于 reviewStatus 的条件。
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus),"reviewStatus",reviewStatus);
        // 模糊匹配：如果 reviewMessage 不为空，则添加 reviewMessage 模糊匹配 reviewMessage 的条件。
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        // 精确匹配：如果 reviewerId 不为空，则添加 reviewerId 等于 reviewerId 的条件。
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId),"spaceId",spaceId);
        queryWrapper.isNull(nullSpaceId,"spaceId");

        // JSON 数组查询：如果 tags 列表不为空，则遍历每个 tag，并添加 JSON 模糊匹配条件。
        // 适用于数据库中 tags 字段存储为 JSON 字符串的情况。
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                // 使用 like 查询包含指定 tag 的 JSON 字符串，例如 "tags" LIKE "%"tag"%"
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序：如果 sortField 不为空，则根据 sortOrder 进行排序。
        // sortOrder.equals("ascend") 判断是否为升序。
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper; // 返回构建好的查询包装器
    }

    /**
     * 将 Picture 实体对象转换为 PictureVO 视图对象。
     * 该方法主要用于数据封装，将数据库实体对象转换为前端所需的数据格式，并关联用户信息。
     *
     * @param picture Picture 实体对象。
     * @param request HttpServletRequest 请求对象 (在此方法中未使用，可能是历史遗留参数)。
     * @return PictureVO 转换后的图片视图对象。
     */
    @Override // 覆盖 PictureService 接口中的 getPictureVO 方法
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 将 Picture 实体对象的基本属性拷贝到 PictureVO 对象中。
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 获取图片所属的用户ID。
        Long userId = picture.getUserId();
        // 如果用户ID有效（不为空且大于0），则关联查询用户信息。
        if(userId != null && userId > 0){
            // 根据用户ID从数据库中获取用户实体。
            User user = userService.getById(userId);
            // 将用户实体转换为用户视图对象。
            UserVO userVO = userService.getUserVO(user);
            // 将用户视图对象设置到图片视图对象中。
            pictureVO.setUser(userVO);
        }

        return pictureVO; // 返回带有用户信息的图片视图对象
    }

    /**
     * 分页获取图片封装（将 Page<Picture> 转换为 Page<PictureVO>）。
     * 该方法处理分页查询的结果，将原始的 Picture 实体分页转换为包含完整用户信息的 PictureVO 分页。
     *
     * @param picturePage 原始的 Picture 实体分页对象。
     * @param request HttpServletRequest 请求对象 (在此方法中未使用，可能是历史遗留参数)。
     * @return Page<PictureVO> 转换后的图片视图对象分页。
     */
    @Override // 覆盖 PictureService 接口中的 getPictureVOPage 方法
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        // 获取当前分页的图片实体列表。
        List<Picture> pictureList = picturePage.getRecords();
        // 创建一个新的 PictureVO 分页对象，并复制原始分页的基本信息（当前页、每页大小、总记录数）。
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        // 如果图片列表为空，则直接返回空的 PictureVO 分页。
        if(CollUtil.isEmpty(pictureList)){
            return pictureVOPage;
        }
        // 将图片实体列表通过 Stream API 转换为图片视图对象列表。
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息：从图片列表中提取所有不重复的用户ID。
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        // 根据用户ID集合批量查询用户，并将结果按用户ID分组，以便快速查找。
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy((User::getId)));
        // 2. 对封装对象列表进行赋值：遍历图片视图对象列表，为每个图片设置关联的用户信息。
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId(); // 获取当前图片的用户ID
            User user = null; // 初始化用户实体为 null
            // 如果用户ID在查询到的用户Map中存在，则获取对应的用户实体。
            if(userIdUserListMap.containsKey(userId)){
                user = userIdUserListMap.get(userId).get(0); // 获取用户列表中的第一个用户（假设ID唯一）
            }
            // 将用户实体转换为用户视图对象并设置到图片视图对象中。
            pictureVO.setUser(userService.getUserVO(user));
        });
        // 将处理后的图片视图对象列表设置到新的分页对象中。
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage; // 返回包含用户信息的图片视图对象分页


    }

    /**
     * 图片校验，用于更新和修改图片时进行判断。
     * 该方法对图片实体中的一些关键字段进行非空和长度校验。
     *
     * @param picture 图片实体对象。
     */
    @Override // 覆盖 PictureService 接口中的 validPicture 方法
    public void validPicture(Picture picture) {
        // 校验：如果图片对象为空，则抛出参数错误异常。
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        // 从图片对象中提取关键字段的值。
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 校验：在修改数据时，图片ID不能为空。如果ID为空，则抛出参数错误异常。
        ThrowUtils.throwIf(ObjUtil.isNull(id),ErrorCode.PARAMS_ERROR,"id不能为空");
        // 校验：如果 URL 不为空，则检查其长度是否超过 1024。
        if(StrUtil.isNotBlank(url)){
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR,"url过长");
        }
        // 校验：如果 introduction 不为空，则检查其长度是否超过 10000。
        if(StrUtil.isNotBlank(introduction)){
            ThrowUtils.throwIf(introduction.length() > 10000, ErrorCode.PARAMS_ERROR,"简介过长");
        }
    }

    /**
     * 图片审核功能。
     * 该方法用于处理管理员对图片进行审核的操作，包括校验参数、判断图片状态、更新审核信息。
     *
     * @param pictureReviewRequest 图片审核请求体，包含图片ID和审核状态。
     * @param loginUser 当前登录的用户，用于记录审核人ID。
     */
    @Override // 覆盖 PictureService 接口中的 doPictureReview 方法
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // 获取图片请求体中的图片ID和审核结果状态值。
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        // 将审核状态值转换为对应的枚举类型。
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        // 校验参数：如果图片ID为空，或审核状态枚举无效，或审核状态是“待审核”（不允许直接设置为待审核），则抛出参数错误异常。
        if(id == null || reviewStatusEnum == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断图片是否存在：根据图片ID从数据库中查询旧的图片信息。
        Picture oldPicture = this.getById(id);
        // 校验：如果旧图片不存在，则抛出未找到异常。
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        // 检查是否重复审核：如果旧的审核状态与当前请求的审核状态相同，则抛出参数错误异常。
        if(oldPicture.getReviewStatus().equals(reviewStatus)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请勿重复审核");
        }

        // 更新图片审核状态：创建一个新的 Picture 对象用于更新。
        Picture updatePicture = new Picture();
        // 将审核请求体中的属性拷贝到更新对象中（如 id, reviewStatus, reviewMessage）。
        BeanUtils.copyProperties(pictureReviewRequest,updatePicture);
        // 设置审核人ID为当前登录用户的ID。
        updatePicture.setReviewerId(loginUser.getId());
        // 设置审核时间为当前时间。
        updatePicture.setReviewTime(new Date());
        // 执行数据库更新操作，根据 ID 更新图片信息。
        boolean result = this.updateById(updatePicture);
        // 校验：如果更新操作失败，则抛出操作失败异常。
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片审核失败");

    }

    /**
     * 异步清理 COS 中的图片文件。
     * 该方法用于在图片被删除或更新后，判断是否需要从 COS 中删除对应的文件，避免冗余存储。
     * 使用 @Async 注解，表示该方法将在一个独立的线程中异步执行，不阻塞主业务流程。
     * @param oldPicture 待清理的旧图片实体对象，包含图片URL和缩略图URL。
     */
    @Async // 标识该方法是异步执行的，由 Spring 的任务执行器管理
    @Override // 覆盖 PictureService 接口中的 clearPictureFile 方法
    public void clearPictureFile(Picture oldPicture){
        // 获取旧图片的 URL。
        String pictureUrl = oldPicture.getUrl();
        // 判断该图片 URL 是否被数据库中多条记录使用。
        // 统计数据库中 URL 字段与当前图片 URL 相同的记录数量。
        Long count = this.lambdaQuery()
                .eq(Picture::getUrl, pictureUrl)
                .count();

        // 如果该图片 URL 被不止一条记录使用（即 count > 1），则不进行清理，因为其他记录还在使用它。
        if(count > 1){
            return;
        }
        // FIXME 注意：这里的 url 包含了域名，实际上删除 COS 对象只需要传 key 值(存储路径) 就够了。
        // 需要从完整的 URL 中提取出 COS 的 key（例如，去除域名部分）。
        // 假设 cosManager.deleteObject 内部会处理 URL 到 Key 的转换，或者这里应该传入 Key。
        cosManager.deleteObject(oldPicture.getUrl()); // 删除 COS 中的主图片文件
        // 获取缩略图 URL。
        String thumbnailUrl = oldPicture.getThumbnailUrl();
        // 如果缩略图 URL 不为空，则尝试删除缩略图。
        if(StrUtil.isNotBlank(thumbnailUrl)){
            cosManager.deleteObject(thumbnailUrl); // 删除 COS 中的缩略图文件
        }
    }


    /**
     * 根据用户信息和上传图片结果创建或更新图片对象。
     * 这是一个辅助方法，用于将上传成功后的图片信息封装成 Picture 实体，并补充用户和审核相关参数。
     *
     * @param loginUser 当前登录的用户，用于关联图片与用户。
     * @param uploadPictureResult 图片上传结果，包含图片的各种元数据和 URL。
     * @param pictureId 图片ID，如果为 null 表示新增图片，否则表示更新现有图片。
     * @return 返回一个新的图片对象，或者更新后的图片对象（包含新上传的信息）。
     */
    private Picture getPicture(User loginUser, UploadPictureResult uploadPictureResult, Long pictureId,Long spaceId) {
        // 创建一个新的 Picture 实体对象。
        Picture picture = new Picture();
        // 设置图片的访问 URL。
        picture.setUrl(uploadPictureResult.getUrl());
        // 设置图片的名称。
        picture.setName(uploadPictureResult.getPicName());
        // 设置图片的大小（字节）。
        picture.setPicSize(uploadPictureResult.getPicSize());
        // 设置图片的宽度。
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        // 设置图片的高度。
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        // 设置图片的缩放比例（宽高比）。
        picture.setPicScale(uploadPictureResult.getPicScale());
        // 设置图片的格式（如 jpg, png, webp）。
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        // 设置图片所属的用户ID。
        picture.setUserId(loginUser.getId());
        // 设置缩略图的访问 URL。
        picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());
        // 设置空间ID
        picture.setSpaceId(spaceId);

        // 再次设置图片格式和用户ID (可能与上面重复，可优化)。
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        // 补充审核参数：根据用户身份（是否管理员）设置图片的初始审核状态和信息。
        fillReviewParams(picture,loginUser);

        // 如果 pictureId 不为空，表示是更新现有图片的操作。
        if(pictureId != null){
            // 打印日志，显示正在更新的图片ID。
            System.out.println("更新图片，ID: " + pictureId);
            // 设置图片ID。
            picture.setId(pictureId);
            // 设置图片编辑时间为当前时间。
            picture.setEditTime(new Date());
        }
        // 打印日志，显示最终要保存或更新的图片信息。
        System.out.println("上传图片信息: " + picture);
        // 返回构建好的图片实体对象。
        return picture;
    }

    /**
     * 该方法用来判断上传图片者的身份，并设置相应的审核状态和信息。
     * 如果是管理员上传，则图片自动过审；否则，设置为待审核状态。
     * @param picture 图片实体对象，待设置审核参数。
     * @param loginUser 当前登录的用户，用于判断其是否为管理员。
     */
    @Override // 覆盖 PictureService 接口中的 fillReviewParams 方法
    public void fillReviewParams(Picture picture, User loginUser){
        // 验证登录用户是否为管理员。
        if (userService.isAdmin(loginUser)) {
            // 如果是管理员，则图片自动设置为“通过”审核状态。
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            // 设置审核人ID为当前管理员的ID。
            picture.setReviewerId(loginUser.getId());
            // 设置审核时间为当前时间。
            picture.setReviewTime(new Date());
            // 设置审核信息为“管理员自动过审”。
            picture.setReviewMessage("管理员自动过审");
        }else {
            // 如果不是管理员，则图片设置为“待审核”状态。
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
            // 设置审核信息为“待审核”。
            picture.setReviewMessage("待审核");
        }
    }

    /**
     * 批量上传图片，使用爬虫抓取关键字图片进行批量下载并上传。
     * 该方法通过 Jsoup 从 Bing 图片搜索结果中抓取图片 URL，然后异步上传到 COS。
     *
     * @param pictureUploadByBatchRequest 图片批量上传请求体，包含搜索文本、名称前缀和数量。
     * @param loginUser 登录用户，用于确定图片所属和上传路径。
     * @return Integer 返回成功上传的图片数量。
     */
    @Override // 覆盖 PictureService 接口中的 uploadPictureByBatch 方法
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        System.out.println("批量上传图片"); // 打印日志，表示开始批量上传
        // 获取搜索文本（关键字），用于 Bing 图片搜索。
        String searchText = pictureUploadByBatchRequest.getSearchText();
        // 获取图片名称前缀。如果为空，则使用搜索文本作为名称前缀。
        String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
        if(StrUtil.isBlank(namePrefix)){
            namePrefix = searchText;
        }
        // 获取要上传的图片数量。
        Integer count = pictureUploadByBatchRequest.getCount();
        // 校验数量范围：如果数量超出 1 到 30 的范围，则抛出参数错误异常。
        ThrowUtils.throwIf(count > 30 || count < 1, ErrorCode.PARAMS_ERROR, "数量超出范围");
        // 生成一个随机偏移量，用于 Bing 搜索结果的分页，避免每次都从头开始抓取相同的图片。
        int offset = RandomUtil.randomInt(1000); // 随机生成 0 到 999 之间的整数
        // 构造要抓取的 Bing 图片搜索页面的 URL。
        // %s 是搜索文本，%d 是偏移量 (first 参数)。
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1&first=%d", searchText,offset);
        Document document; // 声明一个 Jsoup Document 对象，用于存储解析后的 HTML 文档
        try{
            // 使用 Jsoup 连接到目标 URL，并发送 HTTP GET 请求获取图片页面文档。
            // 设置 User-Agent 模拟浏览器访问，以避免被网站识别为爬虫并阻止访问。
            document = Jsoup.connect(fetchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get(); // 执行请求并解析 HTML
        }catch (IOException e){
            // 如果在获取图片页面时发生 IO 异常（如网络问题），则记录错误日志并抛出业务异常。
            log.error("获取图片失败",e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"获取图片失败");
        }
        // 从 HTML 文档中获取包含图片元素的 div 元素。
        // "dgControl" 是 Bing 图片搜索结果页中包含图片列表的 CSS 类名。
        Element div = document.getElementsByClass("dgControl").first();
        // 校验：如果未找到指定的 div 元素，则抛出业务异常。
        if(ObjUtil.isNull(div)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"获取元素失败");
        }
        // 从找到的 div 元素中选择所有包含图片信息的元素。
        // ".iusc" 是 Bing 图片搜索结果中每个图片项的 CSS 类名。
        Elements imgElementList = div.select(".iusc");
        int uploadCount = 0; // 初始化成功上传的图片数量计数器
        // 遍历图片元素列表，逐个处理图片。
        for (Element imgElement : imgElementList) {
            String imgUrl; // 声明图片 URL 变量
            // 获取每个图片元素上存储图片信息的 "m" 属性值，它是一个 JSON 字符串。
            String dataM = imgElement.attr("m");
            try{
                // 解析 JSON 字符串，将其转换为 JSONObject 对象。
                JSONObject jsonObject = JSONUtil.parseObj(dataM);
                // 从 JSONObject 中获取 "murl" 字段的值，这就是原始图片的 URL。
                imgUrl = jsonObject.getStr("murl");
            }catch (Exception e){
                // 如果解析 JSON 字符串失败，则记录错误日志并跳过当前图片，继续处理下一张。
                log.error("解析图片URL失败",e);
                continue;
            }
            // 校验：如果获取到的图片 URL 为空，则记录日志并跳过当前图片。
            if(StrUtil.isBlank(imgUrl)){
                log.info("当前链接为空，已跳过:{}",imgUrl);
                continue;
            }
            // 处理图片 URL：如果 URL 中包含问号（?），表示有查询参数，只取问号之前的部分，避免转义问题或不必要的参数。
            int questionMarkIndex = imgUrl.indexOf("?");
            if(questionMarkIndex > -1){
                imgUrl = imgUrl.substring(0,questionMarkIndex);
            }
            // 创建图片上传请求对象。
            PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
            // 如果设置了名称前缀，则为图片设置名称（名称前缀 + 递增序号）。
            if(StrUtil.isNotBlank(namePrefix)){
                pictureUploadRequest.setPicName(namePrefix + (uploadCount + 1));
            }
            try{
                // 调用 uploadPicture 方法上传图片（这里使用 URL 上传策略）。
                PictureVO pictureVO = this.uploadPicture(imgUrl, pictureUploadRequest, loginUser);
                // 记录图片上传成功的日志，包含图片ID。
                log.info("图片上传成功，id={}",pictureVO.getId());
                uploadCount ++; // 成功上传计数器加一
            }catch (Exception e){
                // 如果图片上传失败，则记录错误日志并继续处理下一张图片。
                log.error("图片上传失败",e);
                continue;
            }
            // 如果成功上传的图片数量达到或超过了请求中指定的数量，则停止循环。
            if (uploadCount >= count){
                break;
            }
        }
        // 返回成功上传的图片总数量。
        return uploadCount;
    }

    /**
     * 校验图片权限 因为删除图片和编辑图片的控制权限是一样的(有删除权限就有编辑权限),可以将这段权限校验逻辑封装成方法
     * @param loginUser
     * @param picture
     */
    @Override
    public void checkPictureAuth(User loginUser, Picture picture){
        Long spaceId = picture.getSpaceId();
        if(spaceId == null){
            // 公共图片，仅本人和管理员可以操作
            if(!picture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }else {
            // 私有空间， 仅空间管理员可操作
            if(!picture.getUserId().equals(loginUser.getId())){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }

    }

    /**
     * 删除图片  这里可能会出现对象存储上的图片文件实际没有被清理的情况，但是对用户来说，不应该感受到"删除图片空间却没有增加"，所以没有将这一步添加到
     * 事务中，可以通过定时任务监测作为补偿措施
     * @param pictureId
     * @param loginUser
     */
    @Override
    public void deletePicture(long pictureId, User loginUser){
        ThrowUtils.throwIf(pictureId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null,ErrorCode.NO_AUTH_ERROR);

        // 判断是否存在
        Picture oldPicture = this.getById(pictureId);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 权限就碍眼
        checkPictureAuth(loginUser,oldPicture);
        // 开启事务
        transactionTemplate.execute(status -> {
            // 操作数据库
            boolean result = this.removeById(pictureId);
            ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);

            // 释放额度
            Long spaceId = oldPicture.getSpaceId();
            if (spaceId != null){
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, spaceId)
                        .setSql("totalSize = totalSize -" + oldPicture.getPicSize())
                        .setSql("totalCount = totalCount - 1")
                        .update();
                ThrowUtils.throwIf(!update,ErrorCode.OPERATION_ERROR,"额度更新失败");
            }
            return true;
        });

        // 异步清理文件
        this.clearPictureFile(oldPicture);
    }

    /**
     * 编辑图片
     * @param pictureEditRequest
     * @param loginUser
     */
    @Override
    public void editPicture(PictureEditRequest pictureEditRequest, User loginUser){
        // 在此处进行实体类和DTO进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest,picture);
        // 注意将list 转为String
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 数据校验
        this.validPicture(picture);
        // 判断是否存在
        Long id = pictureEditRequest.getId();
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 校验权限
        checkPictureAuth(loginUser,oldPicture);
        // 补充审核参数
        this.fillReviewParams(picture,loginUser);
        // 操作数据库
        boolean result = this.updateById(picture);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
    }

}
