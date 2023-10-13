import { useContext } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/confirmation.scss'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {

} from '@fortawesome/free-regular-svg-icons'
import { faCheck, faQuestion, faX } from '@fortawesome/free-solid-svg-icons';
import { ProbesContext } from '../Probes/ProbesTable/ProbesContext';
const Confirm = (props) => {
    const probesContext = useContext(ProbesContext)
    const {confirmContent} = props
    const handleCancel = () =>{
        probesContext.setOpenDeleteScreen(false);
    }
    const handleOk = ()=>{
        probesContext.setOpenDeleteScreen(false);
        probesContext.removeProbe(confirmContent.id)
    }
    return (
        <div className='confirmationScreen'>
            <div className="confirmation">
                <div className='confirmation-content'>
                    {confirmContent.message} <strong>{confirmContent.name}</strong> ?</div>
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