package com.sun.admin.service.impl;

import com.sun.admin.mapper.CategoryMapper;
import com.sun.admin.service.CategoryService;
import com.sun.exception.GraceException;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.pojo.Category;
import com.sun.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

import static com.sun.api.service.BaseService.REDIS_ALL_CATEGORY;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisOperator redisOperator;

    @Transactional
    @Override
    public void createCategory(Category category) {
        int insert = categoryMapper.insert(category);
        if (insert != 1) {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        // 直接使用redis删除缓存即可，用户端在查询的时候会直接查库，再把最新的数据放入到缓存中
        redisOperator.del(REDIS_ALL_CATEGORY);
    }

    @Transactional
    @Override
    public void modifyCategory(Category category) {
        int result = categoryMapper.updateByPrimaryKey(category);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        // 直接使用redis删除缓存即可，用户端在查询的时候会直接查库，再把最新的数据放入到缓存中
        redisOperator.del(REDIS_ALL_CATEGORY);
    }

    @Override
    public boolean queryCatIsExist(String catName, String oldCatName) {
        Example example = new Example(Category.class);
        Example.Criteria catCriteria = example.createCriteria();
        catCriteria.andEqualTo("name", catName);
        if (StringUtils.isNotBlank(oldCatName)) {
            catCriteria.andNotEqualTo("name", oldCatName);
        }

        List<Category> catList = categoryMapper.selectByExample(example);

        boolean isExist = false;
        if (catList != null && !catList.isEmpty() && catList.size() > 0) {
            isExist = true;
        }

        return isExist;
    }

    @Override
    public List<Category> queryCategoryList() {
        return categoryMapper.selectAll();
    }
}
