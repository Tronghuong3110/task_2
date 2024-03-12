
import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/smallconfirm.scss'
const SmallConfirm = (props) => {
    const {setOpenConfirmScreen,action, handleFunction,object} = props;
    const handleCancel = ()=>{
        setOpenConfirmScreen(false)
    }
    const handleOk =()=>{
        setOpenConfirmScreen(false)
        handleFunction(action.module,action.action)
    }
    const checkRender = (arr) =>{
        let result="";
        for(let i=0 ; i< arr.length ;i++){
            if(i<arr.length-1){
                result+=arr[i]+", "
            }
            else result+=arr[i]
            
        }
        return result;
    }
    return (
        <div className='smallConfirmScreen'>
            <div className="smallConfirm">
                <div className="question">Are you sure to {action.action} {action.module.length>1?`${object}s`:object} with id</div>
                <div className="question"><strong>{checkRender(action.module)}</strong> ? </div>
                <div className='note'>{action.note}</div>
                <div className="confirmBtn">
                    <button className="confirmBtn-cancel" onClick={()=>{
                        handleCancel()
                    }}>Cancel</button>
                    <button className="confirmBtn-ok" onClick={()=>{
                        handleOk()
                    }}>Confirm</button>
                </div>
            </div>
        </div>
    )
}
export default SmallConfirm;