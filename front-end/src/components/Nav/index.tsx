import Nav from "./Nav";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import * as userActionCreators from "../../redux/actionCreators/user";
import * as rootTypes from "../../types/index";

const mapStateToProps = (state: rootTypes.IRootState) => ({
  isSignin: state.user.isSignin,
  user: state.user.user
});

const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    signout: () => dispatch(userActionCreators.signout()),
    fetchSetPoint: () => dispatch(userActionCreators.fetchSetPoint())
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Nav);
