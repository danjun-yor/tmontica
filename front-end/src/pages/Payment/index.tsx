import * as React from "react";
import "./styles.scss";
import history from "../../history";
import { RouteComponentProps } from "react-router-dom";
import { ICartMenu } from "../../types/cart";
import { order } from "../../api/order";
import { BASE_URL } from "../../constants";
import { CommonError } from "../../api/CommonError";
import { handleError } from "../../api/common";
import { signout, fetchSetPoint } from "../../redux/actionCreators/user";
import { initializeCart } from "../../redux/actionCreators/cart";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { BaseSyntheticEvent } from "react";
import { ISignoutFunction } from "../../types/user";
import { IInitializeCartFunction } from "../../types/cart";
import { IRootState } from "../../types";

interface MatchParams {
  categoryEng: string;
}

interface IPaymentProps
  extends RouteComponentProps<MatchParams>,
    ISignoutFunction,
    IInitializeCartFunction {
  point: number;
  setPoint(): void;
}

interface IPaymentState {
  totalPrice: number;
  orderPrice: number;
  usedPoint: number;
  usablePoint: number;
  orderCarts: Array<ICartMenu>;
}

const OrderMenu = React.memo(
  ({
    nameKo,
    imgUrl,
    quantity,
    price,
    option
  }: {
    nameKo: string;
    imgUrl: string;
    quantity: number;
    price: number;
    option: string;
  }) => {
    return (
      <li className="order__menu">
        <div className="order__menu-img">
          <img src={`${BASE_URL}${imgUrl}`} alt={nameKo} />
        </div>
        <div className="order__menu-description">
          <div className="order__menu-title-wrap">
            <h3 className="order__menu-title">{nameKo}</h3>
            <div className="order__menu-options">{option}</div>
          </div>
          <div className="order__menu-cnt-price-wrap">
            <div className="order__menu-cnt-wrap d-inline-b">
              <span className="order__menu-cnt">{quantity}</span>개
            </div>
            <div className="order__menu-price-wrap d-inline-b">
              <span className="order__menu-price">{Number(price * quantity).toLocaleString()}</span>
              원
            </div>
          </div>
        </div>
      </li>
    );
  }
);

class Payment extends React.PureComponent<IPaymentProps, IPaymentState> {
  state = {
    totalPrice: 0,
    usedPoint: 0,
    usablePoint: this.props.point,
    orderCarts: [],
    orderPrice: 0
  };

  async order() {
    try {
      const data = await order({
        menus: this.state.orderCarts.map((c: ICartMenu) => {
          return { cartId: typeof c.cartId === "undefined" ? 0 : c.cartId };
        }),
        usedPoint: this.state.usedPoint,
        totalPrice: this.state.orderPrice,
        payment: "현장결제"
      });
      if (data instanceof CommonError) {
        throw data;
      }
      // 바로구매가 아닌 장바구니 구매일 때만 장바구니 상태 초기화
      if (window.localStorage.getItem("isDirect") === "N") {
        await this.props.initializeCart();
      }
      await this.props.setPoint();

      const { orderId } = data;
      history.push(`/orders?orderId=${orderId}`);
    } catch (error) {
      const result = await handleError(error);
      if (result === "signout") {
        this.props.signout();
      } else {
        history.push("/");
      }
    }
  }

  componentDidMount() {
    const orderCarts = JSON.parse(localStorage.getItem("orderCart") || "[]");

    if (!Array.isArray(orderCarts) || (Array.isArray(orderCarts) && orderCarts.length === 0)) {
      alert("주문 정보가 존재하지 않습니다.");
      history.push("/");
      return;
    }
    const orderPrice = this.getOrderPrice(orderCarts);
    this.setState({
      orderCarts,
      orderPrice,
      totalPrice: this.getOrderPrice(orderCarts)
    });
  }

  getOrderPrice(orderCarts: Array<ICartMenu>) {
    return orderCarts.reduce((prev: number, cur: ICartMenu) => cur.price * cur.quantity + prev, 0);
  }

  handlePay = () => {
    if (window.confirm("결제하시겠습니까?")) {
      // TODO: 결제하기 API 호출
      this.order();
      // 내 주문 페이지로 이동
    }
  };

  handleCancle = (e: BaseSyntheticEvent) => {
    e.preventDefault();

    if (window.confirm("취소하시겠습니까?")) {
      localStorage.removeItem("orderCart");
      history.goBack();
    }
  };

  handlePoint = (e: BaseSyntheticEvent) => {
    let willUsedPoint = parseInt(e.currentTarget.value) || 0;

    if (this.state.orderPrice - willUsedPoint < 0) {
      alert("주문금액 보다 많이 입력할 수 없습니다.");
      this.setState(
        {
          usedPoint: 0
        },
        () => {
          this.setState({
            totalPrice: this.state.orderPrice - this.state.usedPoint
          });
        }
      );
      return;
    }
    this.setState(
      {
        usedPoint: willUsedPoint
      },
      () => {
        this.setState({
          totalPrice: this.state.orderPrice - this.state.usedPoint
        });
      }
    );
  };

  render() {
    const { orderCarts, orderPrice } = this.state;

    return (
      <>
        <main className="main">
          <div className="order__container">
            <section className="order card">
              <div className="order__top">
                <h2 className="order__title plr15">주문상품정보</h2>
              </div>
              <div className="order__main">
                <ul className="order__menus">
                  {orderCarts.map((oc: ICartMenu) => {
                    return (
                      <OrderMenu
                        key={oc.cartId}
                        nameKo={oc.nameKo}
                        quantity={oc.quantity}
                        imgUrl={oc.imgUrl}
                        price={oc.price}
                        option={oc.option}
                      />
                    );
                  })}
                </ul>
              </div>
            </section>
            <section className="payment card">
              <div className="payment__top">
                <h2 className="payment__title plr15">결제방법</h2>
              </div>
              <div className="payment__methods button--group">
                <div className="button button--green payment__method active">현장결제</div>
                <div className="button button--green payment__method">카드</div>
              </div>
              <div className="payment__points">
                <input
                  type="text"
                  name="point"
                  value={this.state.usedPoint}
                  onChange={this.handlePoint}
                  aria-label="구매에 사용할 포인트"
                />
                <div>
                  사용가능한 포인트:
                  <span>{(this.state.usablePoint - this.state.usedPoint).toLocaleString()}</span>P
                </div>
              </div>
            </section>

            <section className="price card">
              <div className="d-flex price-row">
                <div className="price--name">주문금액</div>
                <div className="price--value">{orderPrice.toLocaleString()}원</div>
              </div>
              <div className="d-flex price-row">
                <div className="price--name">할인금액</div>
                <div className="price--value">{this.state.usedPoint.toLocaleString()}원</div>
              </div>
              <div className="d-flex price-row">
                <div className="price--name">최종 결제금액</div>
                <div className="price--value">
                  {this.state.totalPrice.toLocaleString() || orderPrice.toLocaleString()}원
                </div>
              </div>
              <div className="button--group">
                <div className="button--cancle button button--green" onClick={this.handleCancle}>
                  취소
                </div>
                <div className="button--pay button button--green" onClick={this.handlePay}>
                  결제하기
                </div>
              </div>
            </section>
          </div>
        </main>
      </>
    );
  }
}

const mapStateToProps = (state: IRootState) => ({
  isSignin: state.user.isSignin,
  localCart: state.cart.localCart,
  cart: state.cart.cart,
  point: state.user.user ? state.user.user.point : 0
});

const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    signout: () => dispatch(signout()),
    initializeCart: () => dispatch(initializeCart()),
    setPoint: () => dispatch(fetchSetPoint())
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Payment);
