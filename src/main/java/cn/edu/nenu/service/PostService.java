package cn.edu.nenu.service;

import cn.edu.nenu.config.orm.jpa.DynamicSpecifications;
import cn.edu.nenu.config.orm.jpa.SearchFilter;
import cn.edu.nenu.domain.Post;
import cn.edu.nenu.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Swingland on 2020/6/24 15:40
 * Tomorrow is a new day!
 */
@Service
public class PostService {
    @Autowired
    PostRepository postRepo;

    /**
     * 根据主键获取实体，常用
     */
    public Post findOne(Integer pkId){
        return postRepo.findOne(pkId);
    }

    /**
     * 保存一个实体，常用
     */
    public Post save(Post entity){
        return postRepo.save(entity);
    }

    /**
     * 批量保存实体  Set，List
     */
    public Iterable<Post> sava(Iterable<Post> entities){
        return postRepo.save(entities);
    }

    /**
     * 根据主键删除实体，常用
     */
    public void remove(Integer pkId){
        postRepo.delete(pkId);
    }

    /**
     * 根据实体删除实体，不常用
     */
    public void remove(Post entity){
        postRepo.delete(entity);
    }

    /**
     * 批量删除实体，使用较少
     */
    public void remove(Iterable<Post> users){
        postRepo.delete(users);
    }

//    /**
//     * 根据字典类型获取字典集合
//     */
//    public List<Post> findByType(String type){
//        return postRepo.findByTypeOrderBySort(type);
//    }

    /**
     * 根据查询条件获取分页结果数据
     */
    public Page<Post> getEntityPage(Map<String, Object> filterParams, int pageNumber, int pageSize){
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
        Specification<Post> spec = buildSpecification(filterParams);
        return postRepo.findAll(spec,pageRequest);
    }
    /**
     * 创建分页请求.
     */
    private PageRequest buildPageRequest(int pageNumber, int pageSize) {
//        Sort sort = null;
//        if ("auto".equals(sortType)) {
//            sort = new Sort(Sort.Post.ASC, "sort");
//        } else if ("sort".equals(sortType)) {
//            sort = new Sort(Sort.Direction.ASC, "sort");
//        }
        return new PageRequest(pageNumber - 1, pageSize);
    }

    /**
     * 创建动态查询条件组合.
     */
    private Specification<Post> buildSpecification(Map<String, Object> filterParams) {

        Map<String, SearchFilter> filters = SearchFilter.parse(filterParams);
        //if (StringUtils.isNotBlank(id)) {
        //    filters.put("id", new SearchFilter("id", SearchFilter.Operator.EQ, id));
        //}
        Specification<Post> spec = DynamicSpecifications.bySearchFilter(filters.values(), Post.class);
        return spec;
    }
}
