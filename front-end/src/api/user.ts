import { TCartAddReq, TCartId, ICart } from "../types/cart";
import { put, post, API_URL, get, withJWT } from "./common";
import { IUserSignupInfo, IUserSigninInfo, IUserSigninActive } from "../types/user";

export function addCart(cartAddReqs: Array<TCartAddReq>) {
  return post<TCartId[]>(`${API_URL}/carts`, cartAddReqs, withJWT());
}

export function getCart() {
  return get<ICart>(`${API_URL}/carts`, withJWT());
}

export function signUp(data: IUserSignupInfo) {
  return post<any>(`${API_URL}/users/signup`, data);
}

export function signIn(data: IUserSigninInfo) {
  return post<{ authorization: string }>(`${API_URL}/users/signin`, data);
}

export function signInActive(params: IUserSigninActive) {
  return get<any>(`${API_URL}/users/active`, { params });
}

export function findId(params: { email: string }) {
  return get<any>(`${API_URL}/users/findid`, { params });
}

export function findIdConfirm(data: { authCode: string }) {
  return post<string>(`${API_URL}/users/findid/confirm`, data);
}

export function findPassword(params: { email: string; id: string }) {
  return get<any>(`${API_URL}/users/findpw`, { params });
}

// 중복 아이디 확인
export function checkDuplicated(id: string) {
  return get<string>(`${API_URL}/users/duplicate/${id}`);
}

export function checkPassword(data: { password: string }) {
  return post<true>(`${API_URL}/users/checkpw`, data, withJWT());
}

export function updateUser(user: { password: string; passwordCheck: string }) {
  return put<true>(`${API_URL}/users`, user, withJWT());
}

export function setPoint() {
  return get<any>(`${API_URL}/points`, withJWT());
}
