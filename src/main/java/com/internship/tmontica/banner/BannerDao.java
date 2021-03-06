package com.internship.tmontica.banner;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BannerDao {

    @Select("SELECT * FROM banners WHERE use_page = #{usePage} AND usable = 1 ORDER BY number")
    List<Banner> getBannersByUsePage(String usePage);

}
