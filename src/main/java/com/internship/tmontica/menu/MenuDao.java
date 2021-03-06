package com.internship.tmontica.menu;

import com.internship.tmontica.option.Option;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface MenuDao {

    @Insert("INSERT INTO menus(name_ko, name_eng, product_price, category_ko, category_eng, monthly_menu, usable," +
                                "img_url, description, sell_price, discount_rate, created_date, creator_id, stock, start_date, end_date)"+
            "VALUES (#{nameKo},#{nameEng}, #{productPrice}, #{categoryKo}, #{categoryEng}, #{monthlyMenu} , #{usable} ,"+
                    "#{imgUrl}, #{description}, #{sellPrice}, #{discountRate}, #{createdDate},  #{creatorId}, #{stock}, #{startDate}, #{endDate})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn = "id")   // 아이디 리턴..
    int addMenu(Menu menu);

    @Insert("INSERT INTO menu_options(menu_id, option_id) VALUES (#{menuId}, #{optionId})")
    int addMenuOption(@Param("menuId")int menuId, @Param("optionId")int optionId);

    @Select("SELECT * FROM menus WHERE id = #{id} AND deleted = 0")
    Menu getMenuById(int id);

    @Select("SELECT * FROM menus ORDER BY created_date DESC")
    List<Menu> getAllMenus();


    @Update("UPDATE menus SET stock = #{stock} WHERE id = #{id}")
    void updateMenuStock(int id, int stock);


    @Select("SELECT * FROM options INNER JOIN menu_options " +
            "ON menu_options.menu_id = #{id} WHERE menu_options.option_id = options.id")
    List<Option> getOptionsById(int id);


}
