import '../../../sass/RecycleBin/NotifyContainer.scss'
import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleCheck, faCircleXmark } from '@fortawesome/free-solid-svg-icons';
const NotifyContainer = (props) => {
    const { notifyList,setOpenNotifyScreen,handleCloseWindow } = props
    return (
        <div className="notifyContainer">
            <div className='notifies'>
                {
                    notifyList.map(notify => {
                        return (
                            <div className='notify d-flex justify-content-between'>
                                <div className='notify-text'>{notify.message}</div>
                                <div className='notify-icon'>
                                    {notify.code==1?<FontAwesomeIcon icon={faCircleCheck} style={{ color: "#0FA958" }}></FontAwesomeIcon>:<FontAwesomeIcon icon={faCircleXmark} style={{ color: "#ca1616", }} />}
                                </div>
                            </div>
                        )
                    })
                }
                <div className='btnConfirm'>
                    <button 
                        onClick={()=>{
                            setOpenNotifyScreen(false)
                            handleCloseWindow()
                        }}
                    >Confirm</button>
                </div>
            </div>
        </div>
    )
}
export default NotifyContainer;