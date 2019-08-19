package com.internship.tmontica.cart;

import com.internship.tmontica.cart.exception.CartException;
import com.internship.tmontica.cart.model.request.CartRequest;
import com.internship.tmontica.cart.model.request.CartUpdateRequest;
import com.internship.tmontica.cart.model.request.CartOptionRequest;
import com.internship.tmontica.cart.model.response.CartIdResponse;
import com.internship.tmontica.cart.model.response.CartResponse;
import com.internship.tmontica.menu.Menu;
import com.internship.tmontica.menu.MenuDao;
import com.internship.tmontica.option.Option;
import com.internship.tmontica.option.OptionDao;
import com.internship.tmontica.order.exception.NotEnoughStockException;
import com.internship.tmontica.security.JwtService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartMenuServiceTest {

    @InjectMocks
    private CartMenuService cartMenuService;

    @Mock
    private CartMenuDao cartMenuDao;
    @Mock
    private OptionDao optionDao;
    @Mock
    private MenuDao menuDao;
    @Mock
    private JwtService jwtService;

    private CartMenu cartMenu;
    private CartMenu cartMenu2;
    private Menu menu;


    @Before
    public void setUp() throws Exception {
        cartMenu = new CartMenu();
        cartMenu.setId(1);
        cartMenu.setMenuId(2);
        cartMenu.setOption("1__1/3__2");
        cartMenu.setPrice(600);
        cartMenu.setQuantity(2);
        cartMenu.setDirect(false);
        cartMenu.setUserId("testid");

        cartMenu2 = new CartMenu();
        cartMenu2.setId(2);
        cartMenu2.setMenuId(2);
        cartMenu2.setOption("");
        cartMenu2.setPrice(600);
        cartMenu2.setQuantity(3);
        cartMenu2.setDirect(false);
        cartMenu2.setUserId("testid");

        menu = new Menu(2, "latte", 2000, "커피", "coffee",
                false, true, "asdfa/asdfa.png", "맛있는 라떼", 1500,
                10,new Date(),new Date() , "admin", "admin",100, "라떼",
                new Date() ,new Date(), false );


    }

    @Test
    public void 카트정보_가져오기() {
        // given
        List<CartMenu> cartMenus = new ArrayList<>();
        cartMenus.add(cartMenu);
        cartMenus.add(cartMenu2);

        when(jwtService.getUserInfo("userInfo")).thenReturn("{\"id\":\"testid\"}");
        when(cartMenuDao.getCartMenuByUserId("testid")).thenReturn(cartMenus);
        DB옵션문자열변환();
        when(menuDao.getMenuById(cartMenu.getMenuId())).thenReturn(menu);

        // when
        CartResponse cartResponse = cartMenuService.getCartMenuApi();
        System.out.println(cartResponse);

        // then
        assertEquals(cartResponse.getSize(), cartMenu.getQuantity()+cartMenu2.getQuantity());
        assertEquals(cartResponse.getMenus().size(), cartMenus.size());
        int totalPrice = 0;
        for (CartMenu cm: cartMenus) {
            totalPrice += (cm.getPrice()+menu.getSellPrice())*cm.getQuantity();
        }
        assertEquals(cartResponse.getTotalPrice(), totalPrice);
    }

    @Test
    public void 카트정보_가져올때_삭제된메뉴_카트에서삭제하기() {
        // given
        List<CartMenu> cartMenus = new ArrayList<>();
        cartMenus.add(cartMenu);

        when(jwtService.getUserInfo("userInfo")).thenReturn("{\"id\":\"testid\"}");
        when(cartMenuDao.getCartMenuByUserId("testid")).thenReturn(cartMenus);
        DB옵션문자열변환();
        when(menuDao.getMenuById(anyInt())).thenReturn(null);

        // when
        cartMenuService.getCartMenuApi();

        // then
        verify(cartMenuDao, atLeastOnce()).deleteCartMenu(1);
    }

    @Test
    public void 카트추가하기() {
        // given
        List<CartRequest> cartRequests = new ArrayList<>();
        List<CartOptionRequest> option = new ArrayList<>();
        option.add(new CartOptionRequest(1,1));
        option.add(new CartOptionRequest(3,2));
        CartRequest cartRequest = new CartRequest(cartMenu.getMenuId(),cartMenu.getQuantity(),option,false);
        cartRequests.add(cartRequest);
        cartRequests.add(cartRequest);
        DB옵션문자열변환();

        when(jwtService.getUserInfo("userInfo")).thenReturn("{\"id\":\"testid\"}");
        when(menuDao.getMenuById(cartRequest.getMenuId())).thenReturn(menu);

        // when
        List<CartIdResponse> cartIds = cartMenuService.addCartApi(cartRequests);
        // 반환되는 카트 id 값들은 auto increment 된 pk 이기 때문에 테스트로 확인을 할 수가 없다...

        // then
        cartMenu.setId(0); // 추가할 때는 0으로 들어감
        verify(cartMenuDao, atLeastOnce()).addCartMenu(cartMenu);
    }

    @Test
    public void 카트추가하기_바로구매(){
        // given
        List<CartRequest> cartRequests = new ArrayList<>();
        List<CartOptionRequest> option = new ArrayList<>();
        option.add(new CartOptionRequest(1,1));
        option.add(new CartOptionRequest(3,2));
        CartRequest cartRequest = new CartRequest(cartMenu.getMenuId(),cartMenu.getQuantity(),option,true);
        cartRequests.add(cartRequest);
        DB옵션문자열변환();

        when(jwtService.getUserInfo("userInfo")).thenReturn("{\"id\":\"testid\"}");
        when(menuDao.getMenuById(cartRequest.getMenuId())).thenReturn(menu);

        // when
        cartMenuService.addCartApi(cartRequests);

        // then
        verify(cartMenuDao, times(1)).deleteDirectCartMenu("testid");
    }

    @Test(expected = NotEnoughStockException.class)
    public void 카트추가하기_재고부족(){
        // given
        List<CartRequest> cartRequests = new ArrayList<>();
        List<CartOptionRequest> option = new ArrayList<>();
        option.add(new CartOptionRequest(1,1));
        option.add(new CartOptionRequest(3,2));
        CartRequest cartRequest = new CartRequest(2,200,option,true);
        cartRequests.add(cartRequest);

        when(jwtService.getUserInfo("userInfo")).thenReturn("{\"id\":\"testid\"}");
        when(menuDao.getMenuById(cartRequest.getMenuId())).thenReturn(menu);

        // when
        cartMenuService.addCartApi(cartRequests);
    }

    @Test(expected = CartException.class)
    public void 카트추가하기_디폴트옵션_선택없을때(){
        // given
        List<CartRequest> cartRequests = new ArrayList<>();
        List<CartOptionRequest> option = new ArrayList<>();
        option.add(new CartOptionRequest(3,1));
        option.add(new CartOptionRequest(4,2));
        CartRequest cartRequest = new CartRequest(2,1,option,true);
        cartRequests.add(cartRequest);

        when(jwtService.getUserInfo("userInfo")).thenReturn("{\"id\":\"testid\"}");
        when(menuDao.getMenuById(cartRequest.getMenuId())).thenReturn(menu);

        // when
        cartMenuService.addCartApi(cartRequests);
    }

    @Test
    public void 카트_수정하기() {
        // given
        int id = cartMenu.getId();
        CartUpdateRequest cartUpdateRequest = new CartUpdateRequest();
        cartUpdateRequest.setQuantity(1);

        when(jwtService.getUserInfo("userInfo")).thenReturn("{\"id\":\"testid\"}");
        when(cartMenuDao.getCartMenuByCartId(id)).thenReturn(cartMenu);
        when(menuDao.getMenuById(2)).thenReturn(menu);

        // when
        cartMenuService.updateCartApi(id, cartUpdateRequest);
        // then
        verify(cartMenuDao, times(1)).updateCartMenuQuantity(id, cartUpdateRequest.getQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void 카트_수정하기_재고부족() {
        // given
        int id = cartMenu.getId();
        CartUpdateRequest cartUpdateRequest = new CartUpdateRequest();
        cartUpdateRequest.setQuantity(1000);

        when(jwtService.getUserInfo("userInfo")).thenReturn("{\"id\":\"testid\"}");
        when(cartMenuDao.getCartMenuByCartId(id)).thenReturn(cartMenu);
        when(menuDao.getMenuById(2)).thenReturn(menu);

        // when
        cartMenuService.updateCartApi(id, cartUpdateRequest);
    }

    @Test(expected = CartException.class)
    public void 카트_수정하기_아이디불일치(){
        // given
        int id = cartMenu.getId();
        CartUpdateRequest cartUpdateRequest = new CartUpdateRequest();
        cartUpdateRequest.setQuantity(1);

        when(jwtService.getUserInfo("userInfo")).thenReturn("{\"id\":\"notvalidId\"}");
        when(cartMenuDao.getCartMenuByCartId(id)).thenReturn(cartMenu);

        // when
        cartMenuService.updateCartApi(id, cartUpdateRequest);
    }

    @Test
    public void 카트_삭제하기() {
        // given
        int id = cartMenu.getId();

        when(jwtService.getUserInfo("userInfo")).thenReturn("{\"id\":\"testid\"}");
        when(cartMenuDao.getCartMenuByCartId(id)).thenReturn(cartMenu);

        // when
        cartMenuService.deleteCartApi(id);
        // then
        verify(cartMenuDao, times(1)).deleteCartMenu(id);
    }

    @Test(expected = CartException.class)
    public void 카트_삭제하기_아이디불일치(){
        // given
        int id = cartMenu.getId();

        when(jwtService.getUserInfo("userInfo")).thenReturn("{\"id\":\"notvalidId\"}");
        when(cartMenuDao.getCartMenuByCartId(id)).thenReturn(cartMenu);

        // when
        cartMenuService.deleteCartApi(id);
    }

    @Test
    public void DB옵션문자열변환() {
        //given
        when(optionDao.getOptionById(1)).thenReturn(new Option("HOT", 0, "Temperature"));
        when(optionDao.getOptionById(2)).thenReturn(new Option("ICE", 0, "Temperature"));
        when(optionDao.getOptionById(3)).thenReturn(new Option("AddShot", 300, "Shot"));
        when(optionDao.getOptionById(4)).thenReturn(new Option("AddSyrup", 300, "Syrup"));
        when(optionDao.getOptionById(5)).thenReturn(new Option("SizeUp", 500, "Size"));

        // when
        String str = cartMenuService.convertOptionStringToCli("1__1/3__2/4__1");
        assertEquals(str, "HOT/샷추가(2개)/시럽추가(1개)");

    }
}