update subline_version s set business_organisation = l.business_organisation from line_version l where s.mainline_slnid=l.slnid;
