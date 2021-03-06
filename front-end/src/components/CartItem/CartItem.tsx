import React, { PureComponent } from "react";
import { numberCommaRegex } from "../../utils";
import { BASE_URL } from "../../constants";
import "./styles.scss";

export interface ICartItemProps {
  id: number | undefined;
  name: string;
  price: number;
  option: string | Object;
  quantity: number;
  imgUrl: string;
  isSignin: boolean;
  removeLocalCart(payload: number): void;
  changeLocalCart(payload: { id: number; quantity: number }): void;
  fetchRemoveCart(payload: number): void;
  fetchChangeCart(payload: { id: number; quantity: number }): void;
}

export interface ICartItemState {
  quantity: number;
}

class CartItem extends PureComponent<ICartItemProps, ICartItemState> {
  state = {
    quantity: this.props.quantity
  };

  buildSelectOptions = () => {
    var arr = [];
    for (let i = 1; i <= 30; i++) {
      arr.push(
        <option key={i} value={i}>
          {i}개
        </option>
      );
    }
    return arr;
  };

  handleQuantityChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const { id, isSignin, changeLocalCart, fetchChangeCart } = this.props;
    if (isSignin) {
      if (id !== undefined) {
        fetchChangeCart({ id: id, quantity: Number(e.currentTarget.value) });
      }
    } else {
      if (id !== undefined) {
        changeLocalCart({ id: id, quantity: Number(e.currentTarget.value) });
      }
    }
    this.setState({
      quantity: Number(e.currentTarget.value)
    });
  };

  render() {
    const { quantity } = this.state;
    const {
      id,
      name,
      price,
      option,
      imgUrl,
      removeLocalCart,
      isSignin,
      fetchRemoveCart
    } = this.props;
    const { buildSelectOptions, handleQuantityChange } = this;

    return (
      <li className="cart__item">
        <img src={`${BASE_URL}${imgUrl}`} alt={name} className="cart__item-img" />
        <div className="cart__item-info">
          <span className="cart__item-name">
            <div className="cart__item-name-span">
              {`${name} / ${numberCommaRegex(price * quantity)}원`}
            </div>
            <span
              className="cart__item-delete"
              onClick={() =>
                window.confirm(`${name} 메뉴를 삭제하시겠습니까?`)
                  ? isSignin
                    ? id !== undefined && fetchRemoveCart(id)
                    : id !== undefined && removeLocalCart(id)
                  : ""
              }
            >
              &times;
            </span>
          </span>
          <span className="cart__item-option">{option || "옵션이 없습니다."}</span>
        </div>
        <div className="cart__item-quantity">
          <select
            className="cart__item-quantity-select"
            name="quantity"
            value={quantity}
            onChange={e => handleQuantityChange(e)}
            aria-label={`${name} 수량 수정`}
          >
            {buildSelectOptions()}
          </select>
        </div>
      </li>
    );
  }
}

export default CartItem;
