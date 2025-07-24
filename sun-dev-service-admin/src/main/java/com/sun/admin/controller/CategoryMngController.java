package com.sun.admin.controller;

import com.sun.admin.service.CategoryService;
import com.sun.api.BaseController;
import com.sun.api.controller.admin.CategoryMngControllerApi;
import com.sun.grace.result.GraceJSONResult;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.pojo.Category;
import com.sun.pojo.bo.SaveCategoryBO;
import com.sun.utils.JsonUtils;
import com.sun.utils.RedisOperator;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class CategoryMngController extends BaseController implements CategoryMngControllerApi {

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private CategoryService categoryService;

    @Override
    public GraceJSONResult saveOrUpdateCategory(@Valid SaveCategoryBO saveCategoryBO, BindingResult bindingResult) {
        // 判断BindingResult是否保存错误的验证信息，如果有，则直接return
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = getErrors(bindingResult);
            return GraceJSONResult.errorMap(errorMap);
        }

        Category newCat = new Category();
        BeanUtils.copyProperties(saveCategoryBO, newCat);
        // id为空新增，不为空修改
        if (saveCategoryBO.getId() == null) {
            // 查询新增的分类名称不能重复存在
            boolean isExist = categoryService.queryCatIsExist(newCat.getName(), null);
            if (!isExist) {
                // 新增到数据库
                categoryService.createCategory(newCat);
            } else {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
            }
        } else {
            // 查询修改的分类名称不能重复存在
            boolean isExist = categoryService.queryCatIsExist(newCat.getName(), saveCategoryBO.getOldName());
            if (!isExist) {
                // 修改到数据库
                categoryService.modifyCategory(newCat);
            } else {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
            }
        }

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult getFriendLinkList() {
        List<Category> categoryList = categoryService.queryCategoryList();
        return GraceJSONResult.ok(categoryList);
    }

    @Override
    public GraceJSONResult getCats() {
        // 先从redis中查询，如果有，则返回，如果没有，则查询数据库库后先放缓存，放返回
        String allCatJson = redisOperator.get(REDIS_ALL_CATEGORY);

        List<Category> categoryList = null;
        if (StringUtils.isBlank(allCatJson)) {
            categoryList = categoryService.queryCategoryList();
            redisOperator.set(REDIS_ALL_CATEGORY, JsonUtils.objectToJson(categoryList));
        } else {
            categoryList = JsonUtils.jsonToList(allCatJson, Category.class);
        }

        return GraceJSONResult.ok(categoryList);
    }
}
