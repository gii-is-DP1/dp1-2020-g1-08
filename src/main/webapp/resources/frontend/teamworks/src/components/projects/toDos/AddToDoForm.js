import { faPlus } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React from "react";
import ToDoApiUtils from "../../../utils/api/ToDoApiUtils";
import Input from "../../forms/Input";
import SubmitError from "../../forms/SubmitError";


class AddToDoForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      inputs: {
        toDo: "",
      },
      errors: {},
      requestError: "",
    };
    this.disable = props.shouldDisable;
  }

  hasErrors = () => {
    const errors = Object.values(this.state.errors);
    const nInputs = Object.keys(this.state.inputs).length;
    let b = errors.some((e) => {
      return e !== "";
    });
    return b;
  };

  validate = (value) => {
    this.setState({
      requestError: "",
    });

    let errorMsg = "";
    if (value === "") errorMsg = "ToDo required";
    else if (value.length > 30) errorMsg = "Too Long toDo";

    this.setState({
      errors: { ...this.state.errors, toDo: errorMsg },
    });
  };

  changeHandler = (field, value) => {
    this.validate(value);
    this.setState({ inputs: { ...this.state.inputs, toDo: value } });
  };

  apiRequestHandler = (toDoTitle) => {
    ToDoApiUtils.addNewPersonalToDo(this.props.milestoneId, {
      title: toDoTitle,
      done: false,
    })
      .then((res) => {
        this.props.setReloadToDos(true);
      })
      .catch((error) => {
        console.log("ERROR: cannot add the new todo");
        console.log(error.response);
        this.setState({
          requestError: error.response.data,
        });
      });
  };

  submitHandler = (event) => {
    event.preventDefault();
    if (!this.hasErrors()) {
      console.log("CREATING NEW TODO");
      let toDoTitle = this.state.inputs.toDo;
      this.setState({ inputs: { ...this.state.inputs, toDo: "" } });
      this.apiRequestHandler(toDoTitle);
      event.target.reset();
    } else {
      console.log("There are errors in this form");
      console.log(this.state.errors);
    }
  };

  render() {
    return (
      <form onSubmit={this.submitHandler}>
        <span className="ToDoAdd" onClick={null}>
          <FontAwesomeIcon icon={faPlus} style={{ color: "lightgray" }} />
        </span>{" "}
        <Input
          placeholder="Add new ToDo ..."
          styleClass="InputNewToDo"
          changeHandler={this.changeHandler}
          error={this.state.errors.toDo}
        />
        <SubmitError
          error={this.state.requestError !== "" && this.state.requestError}
        />
      </form>
    );
  }
}

export default AddToDoForm;
