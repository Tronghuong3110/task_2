import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import '../../sass/Dashboard/Dashboard.scss';
import { faCube, faMagnifyingGlass, faPlusCircle } from '@fortawesome/free-solid-svg-icons';
import { faPlusSquare } from '@fortawesome/free-regular-svg-icons';
import ProbeConnection from './Content/ProbeConnection'
import ModulesStatus from './Content/ModulesStatus';
import ProbeHistory from './Content/ProbeHistory';

const Dashboard =() =>{
    return (
        <div className="dashboard d-flex justify-content-between">
            <div className='ProbeAndModuleContainer'>
                <ProbeConnection></ProbeConnection>
                <ModulesStatus></ModulesStatus>
            </div>
            <div className='ProbeHistoryContainer'>
                <ProbeHistory></ProbeHistory>
            </div>
        </div>
    )
}
export default Dashboard