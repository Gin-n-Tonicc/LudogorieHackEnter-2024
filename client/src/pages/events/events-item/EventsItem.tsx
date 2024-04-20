import { Link } from 'react-router-dom';
import { PageEnum } from '../../../types';

export interface EventsItemProps {
  id: number;
  address: string;
  startDate: Date;
  endDate: Date;
  peopleRegistered: number;
  title: string;
}

function EventsItem(props: EventsItemProps) {
  return (
    <div className="job-item p-4 mb-4">
      <div className="row g-4">
        <div className="col-sm-12 col-md-8 d-flex align-items-center">
          <img
            className="flex-shrink-0 img-fluid border rounded"
            src="img/com-logo-1.jpg"
            alt=""
            style={{ width: 80, height: 80 }}
          />
          <div className="text-start ps-4">
            <h5 className="mb-3">{props.title}</h5>
            <span className="text-truncate me-3">
              <i className="fa fa-map-marker-alt text-primary me-2" />
              {props.address}
            </span>
            <span className="text-truncate me-3">
              <i className="far fa-clock text-primary me-2" />
              {props.startDate.toLocaleString()} -{' '}
              {props.endDate.toLocaleString()}
            </span>
          </div>
        </div>
        <div className="col-sm-12 col-md-4 d-flex flex-column align-items-start align-items-md-end justify-content-center">
          <div className="d-flex mb-3">
            <a className="btn btn-light btn-square me-3" href="">
              <i className="far fa-heart text-primary" />
            </a>
            <Link
              className="btn btn-primary"
              to={PageEnum.EventsSingle.replace(':id', props.id.toString())}>
              View
            </Link>
          </div>
          <small className="text-truncate">
            <i className="far fa-user text-primary me-2" />
            People going: {props.peopleRegistered}
          </small>
        </div>
      </div>
    </div>
  );
}

export default EventsItem;