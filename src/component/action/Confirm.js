import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/confirmation.scss'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {

} from '@fortawesome/free-regular-svg-icons'
import { faCheck, faQuestion, faX } from '@fortawesome/free-solid-svg-icons';
const Confirm = (props) => {
    const {confirmContent,listDelete,setOpenDeleteScreen,handleFunction,object} = props
    const handleCancel = () =>{
        setOpenDeleteScreen(false);
    }
    const handleOk = ()=>{
        setOpenDeleteScreen(false);
        if(listDelete.length != 0) handleFunction(listDelete,true)
        else handleFunction(confirmContent.id,true)
    }
    return (
        <div className='confirmationScreen'>
            <div className="confirmation">
                <div className='confirmation-content'>
                    {confirmContent.message} <strong>{listDelete.length==0?confirmContent.name:"all selected probe modules"}</strong> ?</div>
                <div className='confirmation-note'>{confirmContent.note}</div>
                <hr></hr>
                <div className='confirmation-decision d-flex justify-content-between'>
                    <div className='confirmation-decision-cancle d-flex'>
                        <button className='d-flex align-items-center' onClick={handleCancel}>
                            <FontAwesomeIcon icon={faX} ></FontAwesomeIcon>No, cancel
                        </button>
                    </div>
                    <div className='confirmation-decision-ok'>
                        <button className='d-flex align-items-center' onClick={handleOk}>
                            <FontAwesomeIcon icon={faCheck} ></FontAwesomeIcon>Yes, delete
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}
export default Confirm